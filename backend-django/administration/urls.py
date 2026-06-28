from django.urls import path

from . import views


urlpatterns = [
    path("login/", views.admin_login, name="admin-login"),
    path("logout/", views.admin_logout, name="admin-logout"),
    path("me/", views.admin_me, name="admin-me"),
    path("summary/", views.admin_summary, name="admin-summary"),
    path("students/", views.admin_students, name="admin-students"),
    path("students/<int:student_id>/", views.admin_student_detail, name="admin-student-detail"),
]
