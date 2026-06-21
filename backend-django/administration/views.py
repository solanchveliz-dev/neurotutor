from rest_framework.decorators import api_view
from rest_framework.response import Response


TEMP_STUDENTS = [
    {
        "id": 1,
        "name": "Maria Gonzales",
        "email": "maria.gonzales@neurotutor.test",
        "grade": "6to",
        "section": "A",
        "level": "BASICO",
        "points": 120,
        "status": "active",
    },
    {
        "id": 2,
        "name": "Luis Ramirez",
        "email": "luis.ramirez@neurotutor.test",
        "grade": "6to",
        "section": "B",
        "level": "INTERMEDIO",
        "points": 280,
        "status": "active",
    },
    {
        "id": 3,
        "name": "Ana Torres",
        "email": "ana.torres@neurotutor.test",
        "grade": "5to",
        "section": "A",
        "level": "AVANZADO",
        "points": 430,
        "status": "inactive",
    },
]

TEMP_TOTAL_MODULES = 3


@api_view(["GET"])
def admin_summary(request):
    active_students = sum(1 for student in TEMP_STUDENTS if student["status"] == "active")
    total_students = len(TEMP_STUDENTS)

    return Response(
        {
            "total_students": total_students,
            "active_students": active_students,
            "inactive_students": total_students - active_students,
            "total_modules": TEMP_TOTAL_MODULES,
        }
    )


@api_view(["GET"])
def admin_students(request):
    return Response(TEMP_STUDENTS)


@api_view(["GET"])
def admin_student_detail(request, student_id):
    student = next(
        (item for item in TEMP_STUDENTS if item["id"] == student_id),
        None,
    )

    if student is None:
        return Response({"detail": "Student not found."}, status=404)

    return Response(student)
