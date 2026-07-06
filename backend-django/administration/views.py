from django.conf import settings
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth import get_user_model
from django.core import signing
from django.views.decorators.csrf import csrf_exempt
import logging
import requests
from rest_framework import exceptions
from rest_framework.authentication import BaseAuthentication, SessionAuthentication, get_authorization_header
from rest_framework.decorators import api_view, authentication_classes, permission_classes
from rest_framework.permissions import AllowAny, BasePermission
from rest_framework.response import Response


logger = logging.getLogger(__name__)
ADMIN_ACCESS_TOKEN_MAX_AGE = 60 * 60 * 12
ADMIN_REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 7
ADMIN_TOKEN_SALT = "neurotutor.admin.auth"


class IsDjangoAdmin(BasePermission):
    message = "Se requiere una sesion de administrador valida."

    def has_permission(self, request, view):
        user = request.user
        return bool(user and user.is_authenticated and (user.is_staff or user.is_superuser))


class AdminBearerAuthentication(BaseAuthentication):
    def authenticate(self, request):
        authorization = get_authorization_header(request).split()
        if not authorization:
            return None
        if authorization[0].lower() != b"bearer":
            return None
        if len(authorization) != 2:
            raise exceptions.AuthenticationFailed("Invalid admin authorization header.")

        try:
            token = authorization[1].decode("utf-8")
        except UnicodeError as exc:
            raise exceptions.AuthenticationFailed("Invalid admin token.") from exc

        user = _load_admin_user_from_token(token, max_age=ADMIN_ACCESS_TOKEN_MAX_AGE)
        return (user, None)


def _build_admin_token(user, token_type):
    return signing.TimestampSigner(salt=ADMIN_TOKEN_SALT).sign(f"{token_type}:{user.id}")


def _load_admin_user_from_token(token, max_age):
    try:
        value = signing.TimestampSigner(salt=ADMIN_TOKEN_SALT).unsign(token, max_age=max_age)
        token_type, user_id = value.split(":", 1)
    except (signing.BadSignature, ValueError) as exc:
        raise exceptions.AuthenticationFailed("Invalid or expired admin token.") from exc

    if token_type != "access":
        raise exceptions.AuthenticationFailed("Invalid admin token type.")

    try:
        user = get_user_model().objects.get(pk=user_id)
    except get_user_model().DoesNotExist as exc:
        raise exceptions.AuthenticationFailed("Admin user not found.") from exc

    if not (user.is_active and (user.is_staff or user.is_superuser)):
        raise exceptions.AuthenticationFailed("Admin access denied.")
    return user


def _request_spring(endpoint, method="GET"):
    headers = {}
    proxy_key = settings.SPRING_ADMIN_PROXY_KEY
    logger.info("Django ADMIN_PROXY_KEY exists: %s", bool(proxy_key))
    logger.info("Django ADMIN_PROXY_KEY length: %s", len(proxy_key))
    logger.info("Django SPRING_ADMIN_API_URL: %s", settings.SPRING_ADMIN_API_URL)
    if settings.SPRING_ADMIN_PROXY_KEY:
        headers["X-ADMIN-PROXY-KEY"] = settings.SPRING_ADMIN_PROXY_KEY
    logger.info(
        "Django sends X-ADMIN-PROXY-KEY: %s (length=%s)",
        "X-ADMIN-PROXY-KEY" in headers,
        len(headers.get("X-ADMIN-PROXY-KEY", "")),
    )

    try:
        spring_url = f"{settings.SPRING_ADMIN_API_URL}/{endpoint}"
        logger.info("Django admin proxy request: %s", spring_url)
        spring_response = requests.request(
            method,
            spring_url,
            headers=headers,
            timeout=settings.SPRING_REQUEST_TIMEOUT_SECONDS,
        )
        try:
            payload = spring_response.json()
        except ValueError:
            payload = {"detail": "Spring Boot returned an invalid response."}

        if spring_response.status_code == 403 and payload.get("detail") == "Invalid admin proxy key.":
            payload = {
                **payload,
                "hint": "Django ADMIN_PROXY_KEY or SPRING_ADMIN_PROXY_KEY must match Railway Spring ADMIN_PROXY_KEY.",
            }

        return Response(
            payload,
            status=spring_response.status_code,
        )
    except requests.Timeout:
        return Response(
            {"detail": "Spring Boot service timed out."},
            status=504,
        )
    except requests.RequestException:
        return Response(
            {"detail": "Spring Boot service is currently unavailable."},
            status=503,
        )


def _get_from_spring(endpoint):
    return _request_spring(endpoint, method="GET")


def _delete_from_spring(endpoint):
    return _request_spring(endpoint, method="DELETE")


@csrf_exempt
@api_view(["POST"])
@authentication_classes([])
@permission_classes([AllowAny])
def admin_login(request):
    username = request.data.get("username") or request.data.get("email")
    password = request.data.get("password")

    if not username or not password:
        return Response({"detail": "Usuario y contrasena son obligatorios."}, status=400)

    user = authenticate(request, username=username, password=password)
    if user is None:
        return Response({"detail": "Credenciales administrativas invalidas."}, status=401)
    if not (user.is_staff or user.is_superuser):
        return Response({"detail": "El usuario no tiene acceso administrativo."}, status=403)

    login(request, user)
    return Response({
        **_admin_user_payload(user),
        "access": _build_admin_token(user, "access"),
        "token": _build_admin_token(user, "access"),
        "refresh": _build_admin_token(user, "refresh"),
    })


@csrf_exempt
@api_view(["POST"])
@authentication_classes([])
@permission_classes([AllowAny])
def admin_logout(request):
    logout(request)
    return Response(status=204)


@api_view(["GET"])
@authentication_classes([AdminBearerAuthentication, SessionAuthentication])
@permission_classes([IsDjangoAdmin])
def admin_me(request):
    return Response(_admin_user_payload(request.user))


def _admin_user_payload(user):
    return {
        "id": user.id,
        "username": user.get_username(),
        "email": user.email,
        "is_staff": user.is_staff,
        "is_superuser": user.is_superuser,
    }


@api_view(["GET"])
@authentication_classes([AdminBearerAuthentication, SessionAuthentication])
@permission_classes([IsDjangoAdmin])
def admin_summary(request):
    return _get_from_spring("summary")


@api_view(["GET"])
@authentication_classes([AdminBearerAuthentication, SessionAuthentication])
@permission_classes([IsDjangoAdmin])
def admin_students(request):
    return _get_from_spring("students")


@api_view(["GET"])
@authentication_classes([AdminBearerAuthentication, SessionAuthentication])
@permission_classes([IsDjangoAdmin])
def admin_student_detail(request, student_id):
    return _get_from_spring(f"students/{student_id}")


@api_view(["GET"])
@authentication_classes([AdminBearerAuthentication, SessionAuthentication])
@permission_classes([IsDjangoAdmin])
def admin_chat_conversations(request):
    return _get_from_spring("chat/conversations")


@api_view(["GET"])
@authentication_classes([AdminBearerAuthentication, SessionAuthentication])
@permission_classes([IsDjangoAdmin])
def admin_chat_student(request, student_id):
    return _get_from_spring(f"chat/student/{student_id}")


@api_view(["GET"])
@authentication_classes([AdminBearerAuthentication, SessionAuthentication])
@permission_classes([IsDjangoAdmin])
def admin_chat_statistics(request):
    return _get_from_spring("chat/statistics")


@api_view(["DELETE"])
@authentication_classes([AdminBearerAuthentication, SessionAuthentication])
@permission_classes([IsDjangoAdmin])
def admin_chat_conversation_delete(request, conversation_id):
    return _delete_from_spring(f"chat/conversations/{conversation_id}")
