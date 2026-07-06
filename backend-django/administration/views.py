from django.conf import settings
from django.contrib.auth import authenticate, login, logout
from django.views.decorators.csrf import csrf_exempt
import logging
import requests
from rest_framework.decorators import api_view, authentication_classes, permission_classes
from rest_framework.permissions import AllowAny, BasePermission
from rest_framework.response import Response


logger = logging.getLogger(__name__)


class IsDjangoAdmin(BasePermission):
    message = "Se requiere una sesion de administrador valida."

    def has_permission(self, request, view):
        user = request.user
        return bool(user and user.is_authenticated and (user.is_staff or user.is_superuser))


def _get_from_spring(endpoint):
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
        spring_response = requests.get(
            f"{settings.SPRING_ADMIN_API_URL}/{endpoint}",
            headers=headers,
            timeout=settings.SPRING_REQUEST_TIMEOUT_SECONDS,
        )
        try:
            payload = spring_response.json()
        except ValueError:
            payload = {"detail": "Spring Boot returned an invalid response."}

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
    return Response(_admin_user_payload(user))


@csrf_exempt
@api_view(["POST"])
@authentication_classes([])
@permission_classes([AllowAny])
def admin_logout(request):
    logout(request)
    return Response(status=204)


@api_view(["GET"])
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
@permission_classes([IsDjangoAdmin])
def admin_summary(request):
    return _get_from_spring("summary")


@api_view(["GET"])
@permission_classes([IsDjangoAdmin])
def admin_students(request):
    return _get_from_spring("students")


@api_view(["GET"])
@permission_classes([IsDjangoAdmin])
def admin_student_detail(request, student_id):
    return _get_from_spring(f"students/{student_id}")
