import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getCurrentUser } from "../services/api";

export default function Dashboard() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [message, setMessage] = useState("Loading dashboard...");

  useEffect(() => {
    async function loadUser() {
      const token = sessionStorage.getItem("token");

      if (!token) {
        navigate("/login", { replace: true });
        return;
      }

      try {
        const data = await getCurrentUser(token);
        setUser(data);
        setMessage("");
      } catch {
        sessionStorage.removeItem("token");
        navigate("/login", { replace: true });
      }
    }

    loadUser();
  }, [navigate]);

  function handleLogout() {
    sessionStorage.removeItem("token");
    navigate("/login", { replace: true });
  }

  return (
    <main className="min-h-screen bg-slate-100 p-6">
      <div className="mx-auto max-w-2xl rounded-2xl bg-white p-8 shadow">
        <div className="flex items-center justify-between gap-4">
          <h1 className="text-2xl font-bold text-slate-800">
            Dashboard
          </h1>

          <button
            onClick={handleLogout}
            className="rounded-lg bg-red-600 px-4 py-2 text-white hover:bg-red-700"
          >
            Logout
          </button>
        </div>

        {message && (
          <p className="mt-5 text-slate-600">{message}</p>
        )}

        {user && (
          <div className="mt-6 space-y-3">
            <p className="text-lg">
              Welcome, <strong>{user.email}</strong>
            </p>

            <p className="text-slate-600">
              Roles: {user.roles.join(", ")}
            </p>

            <p className="text-sm text-green-700">
              Your JWT was accepted by the backend.
            </p>
          </div>
        )}
      </div>
    </main>
  );
}

