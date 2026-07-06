from django.urls import path

from . import views


urlpatterns = [
    path("login/", views.admin_login, name="admin-login"),
    path("logout/", views.admin_logout, name="admin-logout"),
    path("me/", views.admin_me, name="admin-me"),
    path("summary/", views.admin_summary, name="admin-summary"),
    path("students/", views.admin_students, name="admin-students"),
    path("students/<int:student_id>/", views.admin_student_detail, name="admin-student-detail"),
    path("chat/conversations/", views.admin_chat_conversations, name="admin-chat-conversations"),
    path("chat/conversations/<str:conversation_id>/", views.admin_chat_conversation_delete, name="admin-chat-conversation-delete"),
    path("chat/student/<int:student_id>/", views.admin_chat_student, name="admin-chat-student"),
    path("chat/statistics/", views.admin_chat_statistics, name="admin-chat-statistics"),
]
