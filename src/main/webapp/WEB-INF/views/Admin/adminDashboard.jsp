<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Admin Dashboard - Resource Booking System</title>


    <style>

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }


        body {
            font-family: Arial, sans-serif;
            background: #f4f6f8;
            color: #333;
        }


        /* ================================
           SIDEBAR
        ================================= */

        .sidebar {

            position: fixed;

            left: 0;
            top: 0;

            width: 240px;
            height: 100vh;

            background: #212529;

            color: white;

            padding: 25px 15px;
        }


        .sidebar-logo {

            text-align: center;

            font-size: 20px;

            font-weight: bold;

            margin-bottom: 35px;
        }


        .menu {

            list-style: none;
        }


        .menu li {

            margin-bottom: 8px;
        }


        .menu a {

            display: block;

            padding: 13px 15px;

            color: #ddd;

            text-decoration: none;

            border-radius: 5px;

            transition: 0.2s;
        }


        .menu a:hover {

            background: #007bff;

            color: white;
        }


        .menu .active {

            background: #007bff;

            color: white;
        }


        /* ================================
           MAIN CONTENT
        ================================= */

        .main {

            margin-left: 240px;

            min-height: 100vh;
        }


        /* ================================
           TOP BAR
        ================================= */

        .topbar {

            height: 65px;

            background: white;

            display: flex;

            align-items: center;

            justify-content: space-between;

            padding: 0 30px;

            box-shadow:
                0 2px 8px rgba(0,0,0,0.08);
        }


        .topbar h2 {

            font-size: 20px;
        }


        .admin-info {

            display: flex;

            align-items: center;

            gap: 15px;
        }


        .admin-name {

            font-weight: bold;
        }


        .logout-btn {

            border: none;

            background: #dc3545;

            color: white;

            padding: 8px 15px;

            border-radius: 5px;

            cursor: pointer;
        }


        .logout-btn:hover {

            background: #bb2d3b;
        }


        /* ================================
           CONTENT
        ================================= */

        .content {

            padding: 30px;
        }


        .welcome {

            margin-bottom: 30px;
        }


        .welcome h1 {

            margin-bottom: 8px;
        }


        .welcome p {

            color: #666;
        }


        /* ================================
           STATISTICS
        ================================= */

        .stats {

            display: grid;

            grid-template-columns:
                repeat(auto-fit, minmax(200px, 1fr));

            gap: 20px;

            margin-bottom: 35px;
        }


        .stat-card {

            background: white;

            padding: 25px;

            border-radius: 10px;

            box-shadow:
                0 3px 10px rgba(0,0,0,0.08);
        }


        .stat-card h3 {

            color: #666;

            font-size: 14px;

            margin-bottom: 12px;
        }


        .stat-number {

            font-size: 30px;

            font-weight: bold;

            color: #007bff;
        }


        /* ================================
           MANAGEMENT CARDS
        ================================= */

        .section-title {

            margin-bottom: 20px;

            font-size: 22px;
        }


        .management-grid {

            display: grid;

            grid-template-columns:
                repeat(auto-fit, minmax(250px, 1fr));

            gap: 20px;
        }


        .management-card {

            background: white;

            padding: 25px;

            border-radius: 10px;

            box-shadow:
                0 3px 10px rgba(0,0,0,0.08);
        }


        .management-card h3 {

            margin-bottom: 10px;
        }


        .management-card p {

            color: #666;

            line-height: 1.5;

            font-size: 14px;

            margin-bottom: 18px;
        }


        .management-card button {

            border: none;

            background: #007bff;

            color: white;

            padding: 10px 15px;

            border-radius: 5px;

            cursor: pointer;
        }


        .management-card button:hover {

            background: #0056b3;
        }


        /* ================================
           RECENT BOOKINGS
        ================================= */

        .recent-section {

            background: white;

            margin-top: 35px;

            padding: 25px;

            border-radius: 10px;

            box-shadow:
                0 3px 10px rgba(0,0,0,0.08);
        }


        table {

            width: 100%;

            border-collapse: collapse;

            margin-top: 15px;
        }


        th,
        td {

            padding: 13px;

            text-align: left;

            border-bottom: 1px solid #eee;
        }


        th {

            background: #f8f9fa;
        }


        .approved {

            color: #198754;

            font-weight: bold;
        }


        .pending {

            color: #ff9800;

            font-weight: bold;
        }


        .rejected {

            color: #dc3545;

            font-weight: bold;
        }


        /* ================================
           RESPONSIVE
        ================================= */

        @media (max-width: 768px) {

            .sidebar {

                width: 70px;

                padding: 20px 5px;
            }


            .sidebar-logo {

                font-size: 0;
            }


            .sidebar-logo::after {

                content: "RBS";

                font-size: 18px;
            }


            .menu a {

                text-align: center;

                font-size: 0;
            }


            .menu a::first-letter {

                font-size: 20px;
            }


            .main {

                margin-left: 70px;
            }


            .content {

                padding: 20px;
            }

        }

    </style>

</head>


<body>


<!-- =================================================
     SIDEBAR
================================================= -->

<aside class="sidebar">


    <div class="sidebar-logo">

        Admin Panel

    </div>


    <ul class="menu">


        <li>

            <a href="/admin"
               class="active">

                Dashboard

            </a>

        </li>


        <li>

            <a href="/admin/resources">

                Resources

            </a>

        </li>


        <li>

            <a href="/admin/bookings">

                Bookings

            </a>

        </li>


        <li>

            <a href="/admin/users">

                Users

            </a>

        </li>


        <li>

            <a href="/admin/reports">

                Reports

            </a>

        </li>


        <li>

            <a href="/admin/profile">

                Profile

            </a>

        </li>


    </ul>

</aside>



<!-- =================================================
     MAIN
================================================= -->

<div class="main">


    <!-- ================= TOPBAR ================= -->

    <header class="topbar">


        <h2>
            Admin Dashboard
        </h2>


        <div class="admin-info">


            <span class="admin-name">
                Administrator
            </span>


            <button
                    class="logout-btn"
                    onclick="logout()">

                Logout

            </button>


        </div>

    </header>



    <!-- ================= CONTENT ================= -->

    <main class="content">


        <!-- Welcome -->

        <section class="welcome">

            <h1>
                Welcome, Administrator
            </h1>

            <p>
                Manage users, resources and bookings
                from the admin dashboard.
            </p>

        </section>



        <!-- ================= STATISTICS ================= -->

        <section class="stats">


            <div class="stat-card">

                <h3>
                    Total Users
                </h3>

                <div class="stat-number">
                    120
                </div>

            </div>


            <div class="stat-card">

                <h3>
                    Total Resources
                </h3>

                <div class="stat-number">
                    35
                </div>

            </div>


            <div class="stat-card">

                <h3>
                    Total Bookings
                </h3>

                <div class="stat-number">
                    450
                </div>

            </div>


            <div class="stat-card">

                <h3>
                    Pending Bookings
                </h3>

                <div class="stat-number">
                    12
                </div>

            </div>


        </section>



        <!-- ================= MANAGEMENT ================= -->

        <h2 class="section-title">

            Management

        </h2>


        <section class="management-grid">


            <!-- Resources -->

            <div class="management-card">

                <h3>
                    Resource Management
                </h3>

                <p>
                    Add, update, delete and manage
                    available resources.
                </p>

                <button
                        onclick="manageResources()">

                    Manage Resources

                </button>

            </div>



            <!-- Users -->

            <div class="management-card">

                <h3>
                    User Management
                </h3>

                <p>
                    View registered users and manage
                    their accounts.
                </p>

                <button
                        onclick="manageUsers()">

                    Manage Users

                </button>

            </div>



            <!-- Bookings -->

            <div class="management-card">

                <h3>
                    Booking Management
                </h3>

                <p>
                    View, approve, reject and manage
                    resource bookings.
                </p>

                <button
                        onclick="manageBookings()">

                    Manage Bookings

                </button>

            </div>



            <!-- Reports -->

            <div class="management-card">

                <h3>
                    Reports
                </h3>

                <p>
                    View booking statistics and
                    system reports.
                </p>

                <button
                        onclick="viewReports()">

                    View Reports

                </button>

            </div>


        </section>



        <!-- ================= RECENT BOOKINGS ================= -->

        <section class="recent-section">


            <h2>
                Recent Bookings
            </h2>


            <table>


                <thead>

                <tr>

                    <th>
                        User
                    </th>

                    <th>
                        Resource
                    </th>

                    <th>
                        Date
                    </th>

                    <th>
                        Status
                    </th>

                </tr>

                </thead>


                <tbody>


                <tr>

                    <td>
                        Rahul
                    </td>

                    <td>
                        Conference Room A
                    </td>

                    <td>
                        30 Aug 2026
                    </td>

                    <td class="approved">
                        Approved
                    </td>

                </tr>


                <tr>

                    <td>
                        Priya
                    </td>

                    <td>
                        Projector
                    </td>

                    <td>
                        31 Aug 2026
                    </td>

                    <td class="pending">
                        Pending
                    </td>

                </tr>


                <tr>

                    <td>
                        Amit
                    </td>

                    <td>
                        Meeting Room B
                    </td>

                    <td>
                        01 Sep 2026
                    </td>

                    <td class="rejected">
                        Rejected
                    </td>

                </tr>


                </tbody>


            </table>


        </section>


    </main>

</div>



<!-- =================================================
     JAVASCRIPT
================================================= -->

<script>


    function manageResources() {

        window.location.href =
            "/admin/resources";

    }


    function manageUsers() {

        window.location.href =
            "/admin/users";

    }


    function manageBookings() {

        window.location.href =
            "/admin/bookings";

    }


    function viewReports() {

        window.location.href =
            "/admin/reports";

    }


    function logout() {

        localStorage.removeItem("token");

        window.location.href =
            "/login";

    }


</script>


</body>

</html>