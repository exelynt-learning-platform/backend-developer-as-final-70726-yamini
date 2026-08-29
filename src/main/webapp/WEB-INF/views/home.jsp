<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Dashboard - Resource Booking System</title>


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


        /* =========================================
           NAVBAR
        ========================================= */

        .navbar {
            height: 65px;

            background: #007bff;

            color: white;

            display: flex;

            align-items: center;

            justify-content: space-between;

            padding: 0 30px;

            position: sticky;

            top: 0;

            z-index: 1000;
        }


        .logo {
            font-size: 21px;
            font-weight: bold;
        }


        .nav-right {
            display: flex;
            align-items: center;
            gap: 25px;
        }


        .nav-link {
            color: white;

            text-decoration: none;

            font-size: 15px;
        }


        .nav-link:hover {
            text-decoration: underline;
        }


        .notification {
            position: relative;

            cursor: pointer;

            font-size: 20px;
        }


        .notification-count {
            position: absolute;

            top: -8px;

            right: -8px;

            background: red;

            color: white;

            width: 17px;

            height: 17px;

            border-radius: 50%;

            font-size: 11px;

            display: flex;

            align-items: center;

            justify-content: center;
        }


        .logout-btn {
            background: white;

            color: #007bff;

            border: none;

            padding: 8px 16px;

            border-radius: 5px;

            cursor: pointer;

            font-weight: bold;
        }


        .logout-btn:hover {
            background: #e9ecef;
        }


        /* =========================================
           MAIN CONTAINER
        ========================================= */

        .main {
            max-width: 1200px;

            margin: auto;

            padding: 35px 25px;
        }


        /* =========================================
           WELCOME SECTION
        ========================================= */

        .welcome-section {
            margin-bottom: 30px;
        }


        .welcome-section h1 {
            font-size: 28px;

            margin-bottom: 8px;
        }


        .welcome-section p {
            color: #666;

            font-size: 15px;
        }


        /* =========================================
           SEARCH
        ========================================= */

        .search-section {
            background: white;

            padding: 20px;

            border-radius: 10px;

            margin-bottom: 30px;

            box-shadow: 0 3px 10px rgba(0,0,0,0.08);
        }


        .search-section h3 {
            margin-bottom: 15px;
        }


        .search-box {
            display: flex;

            gap: 10px;
        }


        .search-box input {
            flex: 1;

            padding: 12px;

            border: 1px solid #ccc;

            border-radius: 5px;

            outline: none;
        }


        .search-box button {
            padding: 12px 20px;

            border: none;

            border-radius: 5px;

            background: #007bff;

            color: white;

            cursor: pointer;
        }


        .search-box button:hover {
            background: #0056b3;
        }


        /* =========================================
           STATISTICS
        ========================================= */

        .stats {
            display: grid;

            grid-template-columns:
                repeat(auto-fit, minmax(200px, 1fr));

            gap: 20px;

            margin-bottom: 35px;
        }


        .stat-card {
            background: white;

            padding: 22px;

            border-radius: 10px;

            box-shadow:
                0 3px 10px rgba(0,0,0,0.08);
        }


        .stat-card h3 {
            color: #666;

            font-size: 14px;

            margin-bottom: 10px;
        }


        .stat-number {
            font-size: 30px;

            font-weight: bold;

            color: #007bff;
        }


        /* =========================================
           SECTION TITLE
        ========================================= */

        .section-title {
            margin-bottom: 18px;

            font-size: 22px;
        }


        /* =========================================
           QUICK ACTION CARDS
        ========================================= */

        .cards {
            display: grid;

            grid-template-columns:
                repeat(auto-fit, minmax(240px, 1fr));

            gap: 22px;

            margin-bottom: 40px;
        }


        .card {
            background: white;

            padding: 25px;

            border-radius: 10px;

            box-shadow:
                0 3px 10px rgba(0,0,0,0.08);

            transition: 0.2s;
        }


        .card:hover {
            transform: translateY(-3px);

            box-shadow:
                0 6px 15px rgba(0,0,0,0.12);
        }


        .card-icon {
            font-size: 35px;

            margin-bottom: 15px;
        }


        .card h3 {
            margin-bottom: 10px;
        }


        .card p {
            color: #666;

            font-size: 14px;

            line-height: 1.5;

            margin-bottom: 18px;
        }


        .card button {
            border: none;

            background: #007bff;

            color: white;

            padding: 10px 15px;

            border-radius: 5px;

            cursor: pointer;
        }


        .card button:hover {
            background: #0056b3;
        }


        /* =========================================
           UPCOMING BOOKING
        ========================================= */

        .booking-section {
            background: white;

            padding: 25px;

            border-radius: 10px;

            box-shadow:
                0 3px 10px rgba(0,0,0,0.08);

            margin-bottom: 35px;
        }


        .booking {
            display: flex;

            justify-content: space-between;

            align-items: center;

            padding: 18px;

            border: 1px solid #ddd;

            border-radius: 8px;

            margin-top: 15px;
        }


        .booking-info h3 {
            margin-bottom: 8px;
        }


        .booking-info p {
            color: #666;

            margin: 4px 0;

            font-size: 14px;
        }


        .booking-status {
            background: #d4edda;

            color: #155724;

            padding: 7px 12px;

            border-radius: 20px;

            font-size: 13px;
        }


        /* =========================================
           RECENT BOOKINGS
        ========================================= */

        .table-container {
            background: white;

            padding: 25px;

            border-radius: 10px;

            box-shadow:
                0 3px 10px rgba(0,0,0,0.08);

            overflow-x: auto;
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

            font-size: 14px;
        }


        th {
            background: #f8f9fa;
        }


        .status-confirmed {
            color: #198754;

            font-weight: bold;
        }


        .status-cancelled {
            color: #dc3545;

            font-weight: bold;
        }


        .status-pending {
            color: #ff9800;

            font-weight: bold;
        }


        /* =========================================
           FOOTER
        ========================================= */

        .footer {
            text-align: center;

            padding: 25px;

            margin-top: 40px;

            background: #343a40;

            color: white;

            font-size: 14px;
        }


        /* =========================================
           RESPONSIVE
        ========================================= */

        @media (max-width: 768px) {

            .navbar {
                padding: 0 15px;
            }


            .nav-link {
                display: none;
            }


            .main {
                padding: 25px 15px;
            }


            .search-box {
                flex-direction: column;
            }


            .booking {
                flex-direction: column;

                align-items: flex-start;

                gap: 15px;
            }

        }

    </style>

</head>


<body>


<!-- =================================================
     NAVBAR
================================================= -->

<nav class="navbar">

    <div class="logo">
        Resource Booking System
    </div>


    <div class="nav-right">

        <a class="nav-link" href="/home">
            Home
        </a>

        <a class="nav-link" href="/resources">
            Resources
        </a>

        <a class="nav-link" href="/bookings">
            My Bookings
        </a>

        <a class="nav-link" href="/profile">
            Profile
        </a>


        <div class="notification"
             onclick="showNotifications()">

            🔔

            <span class="notification-count">
                2
            </span>

        </div>


        <button
                class="logout-btn"
                onclick="logout()">

            Logout

        </button>

    </div>

</nav>



<!-- =================================================
     MAIN
================================================= -->

<main class="main">


    <!-- ================= WELCOME ================= -->

    <section class="welcome-section">

        <h1>
            Welcome to Resource Booking System
        </h1>

        <p>
            Manage your resources and bookings from one place.
        </p>

    </section>



    <!-- ================= SEARCH ================= -->

    <section class="search-section">

        <h3>
            Find a Resource
        </h3>


        <div class="search-box">

            <input
                    type="text"
                    id="searchInput"
                    placeholder="Search rooms, meeting rooms, equipment...">


            <button onclick="searchResource()">

                Search

            </button>

        </div>

    </section>



    <!-- ================= STATISTICS ================= -->

    <h2 class="section-title">
        My Dashboard
    </h2>


    <section class="stats">


        <div class="stat-card">

            <h3>
                Total Bookings
            </h3>

            <div class="stat-number">
                12
            </div>

        </div>


        <div class="stat-card">

            <h3>
                Upcoming Bookings
            </h3>

            <div class="stat-number">
                3
            </div>

        </div>


        <div class="stat-card">

            <h3>
                Completed Bookings
            </h3>

            <div class="stat-number">
                8
            </div>

        </div>


        <div class="stat-card">

            <h3>
                Available Resources
            </h3>

            <div class="stat-number">
                24
            </div>

        </div>


    </section>



    <!-- ================= QUICK ACTIONS ================= -->

    <h2 class="section-title">
        Quick Actions
    </h2>


    <section class="cards">


        <!-- Resources -->

        <div class="card">

            <div class="card-icon">
                🏢
            </div>

            <h3>
                Resources
            </h3>

            <p>
                Browse all available rooms,
                meeting rooms and equipment.
            </p>

            <button onclick="goToResources()">

                View Resources

            </button>

        </div>



        <!-- Book Resource -->

        <div class="card">

            <div class="card-icon">
                📅
            </div>

            <h3>
                Book Resource
            </h3>

            <p>
                Select an available resource
                and create a new booking.
            </p>

            <button onclick="goToBookings()">

                Book Now

            </button>

        </div>



        <!-- My Bookings -->

        <div class="card">

            <div class="card-icon">
                📋
            </div>

            <h3>
                My Bookings
            </h3>

            <p>
                View your upcoming,
                completed and cancelled bookings.
            </p>

            <button onclick="goToMyBookings()">

                View Bookings

            </button>

        </div>



        <!-- Profile -->

        <div class="card">

            <div class="card-icon">
                👤
            </div>

            <h3>
                My Profile
            </h3>

            <p>
                View and update your account
                information.
            </p>

            <button onclick="goToProfile()">

                View Profile

            </button>

        </div>


    </section>



    <!-- ================= UPCOMING BOOKING ================= -->

    <h2 class="section-title">
        Upcoming Booking
    </h2>


    <section class="booking-section">


        <div class="booking">


            <div class="booking-info">

                <h3>
                    Conference Room A
                </h3>

                <p>
                    Date: 30 August 2026
                </p>

                <p>
                    Time: 10:00 AM - 12:00 PM
                </p>

                <p>
                    Location: Pune Office
                </p>

            </div>


            <div class="booking-status">

                Confirmed

            </div>


        </div>


    </section>



    <!-- ================= RECENT BOOKINGS ================= -->

    <h2 class="section-title">
        Recent Bookings
    </h2>


    <section class="table-container">


        <table>

            <thead>

            <tr>

                <th>
                    Resource
                </th>

                <th>
                    Date
                </th>

                <th>
                    Time
                </th>

                <th>
                    Status
                </th>

            </tr>

            </thead>


            <tbody>


            <tr>

                <td>
                    Meeting Room A
                </td>

                <td>
                    28 Aug 2026
                </td>

                <td>
                    10:00 AM - 11:00 AM
                </td>

                <td class="status-confirmed">
                    Confirmed
                </td>

            </tr>


            <tr>

                <td>
                    Projector
                </td>

                <td>
                    25 Aug 2026
                </td>

                <td>
                    02:00 PM - 04:00 PM
                </td>

                <td class="status-confirmed">
                    Completed
                </td>

            </tr>


            <tr>

                <td>
                    Conference Room B
                </td>

                <td>
                    20 Aug 2026
                </td>

                <td>
                    11:00 AM - 01:00 PM
                </td>

                <td class="status-cancelled">
                    Cancelled
                </td>

            </tr>


            </tbody>

        </table>


    </section>


</main>



<!-- =================================================
     FOOTER
================================================= -->

<footer class="footer">

    © 2026 Resource Booking System.
    All Rights Reserved.

</footer>



<!-- =================================================
     JAVASCRIPT
================================================= -->

<script>


    /* =========================================
       NAVIGATION
    ========================================= */


    function goToResources() {

        window.location.href = "/resources";

    }


    function goToBookings() {

        window.location.href = "/bookings";

    }


    function goToMyBookings() {

        window.location.href = "/bookings/my";

    }


    function goToProfile() {

        window.location.href = "/profile";

    }


    /* =========================================
       SEARCH
    ========================================= */


    function searchResource() {

        const searchValue =
            document.getElementById("searchInput").value.trim();


        if (searchValue === "") {

            alert("Please enter a resource name.");

            return;

        }


        console.log(
            "Searching resource:",
            searchValue
        );


        /*
         * Later we can replace this with:
         *
         * fetch("/api/resources?search=" + searchValue)
         *
         */


        window.location.href =
            "/resources?search=" +
            encodeURIComponent(searchValue);

    }


    /* =========================================
       NOTIFICATIONS
    ========================================= */


    function showNotifications() {

        alert(
            "You have 2 new notifications."
        );

    }


    /* =========================================
       LOGOUT
    ========================================= */


    function logout() {

        /*
         * Remove JWT token
         */

        localStorage.removeItem("token");


        /*
         * Redirect to login page
         */

        window.location.href = "/login";

    }


    /* =========================================
       PAGE LOAD
    ========================================= */


    document.addEventListener(
        "DOMContentLoaded",
        function() {

            console.log(
                "Home page loaded"
            );


            /*
             * Check whether JWT exists
             */

            const token =
                localStorage.getItem("token");


            if (!token) {

                console.log(
                    "JWT token not found."
                );

                /*
                 * For now we don't redirect here
                 * because /home may be public
                 * during development.
                 *
                 * Later we can implement
                 * proper JWT protection.
                 */

            }

        }
    );

</script>


</body>

</html>