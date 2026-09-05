<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>Login - Resource Booking System</title>

    <style>

        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
            background: #f4f6f8;
        }

        .container {
            width: 350px;
            margin: 100px auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 7px;
            font-weight: bold;
        }

        input {
            width: 100%;
            padding: 10px;
            box-sizing: border-box;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        button {
            width: 100%;
            padding: 11px;
            border: none;
            border-radius: 5px;
            background: #007bff;
            color: white;
            font-size: 16px;
            cursor: pointer;
        }

        button:hover {
            background: #0056b3;
        }

        .register {
            text-align: center;
            margin-top: 20px;
        }

        .register a {
            text-decoration: none;
            color: #007bff;
        }

        #message {
            margin-top: 15px;
            text-align: center;
            font-weight: bold;
        }

    </style>

</head>


<body>

<div class="container">

    <h2>Resource Booking System</h2>


    <form id="loginForm">

        <div class="form-group">

            <label for="email">
                Email
            </label>

            <input
                    type="email"
                    id="email"
                    placeholder="Enter your email"
                    required>

        </div>


        <div class="form-group">

            <label for="password">
                Password
            </label>

            <input
                    type="password"
                    id="password"
                    placeholder="Enter your password"
                    required>

        </div>


        <button type="submit">
            Login
        </button>

    </form>


    <div id="message"></div>


    <div class="register">

        Don't have an account?

        <a href="/register">
            Register
        </a>

    </div>

</div>


<script>

    document.getElementById("loginForm")
        .addEventListener("submit", async function(event) {

            // Prevent normal form submission
            event.preventDefault();

            // Get email
            const email =
                document.getElementById("email").value;

            // Get password
            const password =
                document.getElementById("password").value;

            // Create JSON object
            const loginData = {
                email: email,
                password: password
            };

            try {

                // Call Login REST API
                const response = await fetch(
                    "/api/auth/login",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type": "application/json"
                        },

                        body: JSON.stringify(loginData)
                    }
                );

                // Read JSON response
                const data =
                    await response.json();

                console.log("Login Response:", data);


                // =================================================
                // LOGIN SUCCESS
                // =================================================
                if (response.ok) {

                    // Save JWT token
                    if (data.token) {

                        localStorage.setItem(
                            "token",
                            data.token
                        );
                    }

                    // Save role
                    if (data.role) {

                        localStorage.setItem(
                            "role",
                            data.role
                        );
                    }

                    // Show message
                    document.getElementById("message")
                        .innerText =
                        "Login successful. Redirecting...";


                    // =================================================
                    // ROLE BASED REDIRECTION
                    // =================================================

                    if (data.role === "ROLE_ADMIN") {

                        // Admin
                        window.location.href = "/admin";

                    } else if (data.role === "ROLE_USER") {

                        // Normal user
                        window.location.href = "/home";

                    } else {

                        // Unknown role
                        document.getElementById("message")
                            .innerText =
                            "Invalid user role.";
                    }


                } else {

                    // =================================================
                    // LOGIN FAILED
                    // =================================================

                    document.getElementById("message")
                        .innerText =
                        data.message || "Login failed.";
                }

            } catch (error) {

                console.error(
                    "Login error:",
                    error
                );

                document.getElementById("message")
                    .innerText =
                    "Something went wrong.";
            }

        });

</script>

</body>

</html>