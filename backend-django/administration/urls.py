from django.urls import path

from . import views


urlpatterns = [
    path("summary/", views.admin_summary, name="admin-summary"),
    path("students/", views.admin_students, name="admin-students"),
    path("students/<int:student_id>/", views.admin_student_detail, name="admin-student-detail"),
]
