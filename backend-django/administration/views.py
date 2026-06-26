import requests
from rest_framework.decorators import api_view
from rest_framework.response import Response


SPRING_ADMIN_API_URL = "https://neurotutor-production.up.railway.app/api/admin"
SPRING_REQUEST_TIMEOUT_SECONDS = 5


def _get_from_spring(endpoint):
    try:
        spring_response = requests.get(
            f"{SPRING_ADMIN_API_URL}/{endpoint}",
            timeout=SPRING_REQUEST_TIMEOUT_SECONDS,
        )
        return Response(
            spring_response.json(),
            status=spring_response.status_code,
        )
    except (requests.RequestException, ValueError):
        return Response(
            {"detail": "Spring Boot service is currently unavailable."},
            status=503,
        )


@api_view(["GET"])
def admin_summary(request):
    return _get_from_spring("summary")


@api_view(["GET"])
def admin_students(request):
    return _get_from_spring("students")


@api_view(["GET"])
def admin_student_detail(request, student_id):
    return _get_from_spring(f"students/{student_id}")
