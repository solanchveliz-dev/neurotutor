from unittest.mock import Mock, patch

from django.contrib.auth import get_user_model
from django.test import Client, TestCase


class AdminApiTests(TestCase):
    def setUp(self):
        self.admin = get_user_model().objects.create_user(
            username="admin",
            password="safe-password",
            is_staff=True,
        )

    def test_admin_endpoints_reject_anonymous_users(self):
        response = self.client.get("/api/admin/summary/")
        self.assertEqual(response.status_code, 403)

    def test_staff_user_can_login_and_read_identity(self):
        response = self.client.post(
            "/api/admin/login/",
            {"username": "admin", "password": "safe-password"},
            content_type="application/json",
        )
        self.assertEqual(response.status_code, 200)
        self.assertIn("access", response.json())
        self.assertIn("token", response.json())
        self.assertIn("refresh", response.json())

        response = self.client.get("/api/admin/me/")
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["username"], "admin")

    def test_staff_user_can_read_identity_with_bearer_token(self):
        response = self.client.post(
            "/api/admin/login/",
            {"username": "admin", "password": "safe-password"},
            content_type="application/json",
        )
        access_token = response.json()["access"]

        anonymous_client = Client()
        response = anonymous_client.get(
            "/api/admin/me/",
            HTTP_AUTHORIZATION=f"Bearer {access_token}",
        )

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["username"], "admin")

    def test_admin_login_does_not_require_csrf_token(self):
        csrf_client = Client(enforce_csrf_checks=True)

        response = csrf_client.post(
            "/api/admin/login/",
            {"username": "admin", "password": "safe-password"},
            content_type="application/json",
        )

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["username"], "admin")

    def test_invalid_admin_credentials_return_401_without_csrf_token(self):
        csrf_client = Client(enforce_csrf_checks=True)

        response = csrf_client.post(
            "/api/admin/login/",
            {"username": "admin", "password": "incorrect-password"},
            content_type="application/json",
        )

        self.assertEqual(response.status_code, 401)

    @patch("administration.views.requests.get")
    def test_staff_user_can_use_spring_proxy(self, spring_get):
        spring_response = Mock(status_code=200)
        spring_response.json.return_value = {"total_students": 3}
        spring_get.return_value = spring_response
        self.client.force_login(self.admin)

        response = self.client.get("/api/admin/summary/")

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["total_students"], 3)
        self.assertIn("X-ADMIN-PROXY-KEY", spring_get.call_args.kwargs["headers"])

    @patch("administration.views.requests.get")
    def test_invalid_spring_proxy_key_returns_actionable_hint(self, spring_get):
        spring_response = Mock(status_code=403)
        spring_response.json.return_value = {"detail": "Invalid admin proxy key."}
        spring_get.return_value = spring_response
        self.client.force_login(self.admin)

        response = self.client.get("/api/admin/summary/")

        self.assertEqual(response.status_code, 403)
        self.assertEqual(response.json()["detail"], "Invalid admin proxy key.")
        self.assertIn("Railway Spring ADMIN_PROXY_KEY", response.json()["hint"])
