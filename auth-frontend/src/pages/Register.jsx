import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../services/api";

export default function Register() 
{
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
  });

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  function handleChange(event) {
    setForm({
      ...form,
      [event.target.name]: event.target.value,
    });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setMessage("");
    setLoading(true);

    try {
      const result = await registerUser(form);
      setMessage(result.message);
      setForm({ name: "", email: "", password: "" });

      setTimeout(() => navigate("/login"), 1000);
    } 
    catch (error) 
    {
      setMessage(error.message);
    } 
    finally 
    {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100 px-4">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-md space-y-4 rounded-2xl bg-white p-8 shadow-lg"
      >
        <h1 className="text-3xl font-bold text-slate-800">
          Create account
        </h1>

        <p className="text-slate-500">
          Register to get started.
        </p>

        <input
          className="w-full rounded-lg border p-3"
          name="name"
          placeholder="Full name"
          value={form.name}
          onChange={handleChange}
          required
          maxLength={80}
        />

        <input
          className="w-full rounded-lg border p-3"
          name="email"
          type="email"
          placeholder="Email address"
          value={form.email}
          onChange={handleChange}
          required
          maxLength={180}
        />

        <input
          className="w-full rounded-lg border p-3"
          name="password"
          type="password"
          placeholder="Password (minimum 8 characters)"
          value={form.password}
          onChange={handleChange}
          required
          minLength={8}
          maxLength={72}
        />

        {message && (
          <p role="status" className="text-sm text-blue-700">
            {message}
          </p>
        )}

        <button
          className="w-full rounded-lg bg-blue-600 p-3 font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
          disabled={loading}
        >
          {loading ? "Creating account..." : "Register"}
        </button>

        <p className="text-center text-sm text-slate-600">
          Already registered?{" "}
          <Link
            to="/login"
            className="font-semibold text-blue-600"
          >
            Login
          </Link>
        </p>
      </form>
    </div>
  );
}
