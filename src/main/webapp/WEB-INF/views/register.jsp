<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - Resource Booking System</title>

    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
            background: #f4f6f8;
        }

        .container {
            width: 380px;
            margin: 60px auto;
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
            margin-bottom: 16px;
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
            font-size: 14px;
        }

        input:focus {
            outline: none;
            border-color: #007bff;
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

        .login {
            text-align: center;
            margin-top: 20px;
        }

        .login a {
            text-decoration: none;
            color: #007bff;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Create Account</h2>

    <form action="/api/auth/register" method="post">

        <div class="form-group">

            <label for="name">Full Name</label>

            <input
                    type="text"
                    id="name"
                    name="name"
                    placeholder="Enter your full name"
                    required>

        </div>


        <div class="form-group">

            <label for="email">Email</label>

            <input
                    type="email"
                    id="email"
                    name="email"
                    placeholder="Enter your email"
                    required>

        </div>


        <div class="form-group">

            <label for="password">Password</label>

            <input
                    type="password"
                    id="password"
                    name="password"
                    placeholder="Enter your password"
                    required>

        </div>


        <div class="form-group">

            <label for="confirmPassword">Confirm Password</label>

            <input
                    type="password"
                    id="confirmPassword"
                    name="confirmPassword"
                    placeholder="Confirm your password"
                    required>

        </div>


        <button type="submit">
            Register
        </button>

    </form>


    <div class="login">

        Already have an account?

        <a href="/login">
            Login
        </a>

    </div>

</div>

</body>
</html>