<%@ page import="br.ufu.facom.ereno.api.SetupIED" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="br.ufu.facom.ereno.Util" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">

    <title>Settings - ERENO</title>
    <meta content="" name="description">
    <meta content="" name="keywords">

    <!-- Favicons -->
    <link href="../assets/img/favicon.png" rel="icon">
    <link href="../assets/img/apple-touch-icon.png" rel="apple-touch-icon">

    <!-- Google Fonts -->
    <link href="https://fonts.gstatic.com" rel="preconnect">
    <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,600,600i,700,700i|Nunito:300,300i,400,400i,600,600i,700,700i|Poppins:300,300i,400,400i,500,500i,600,600i,700,700i"
          rel="stylesheet">

    <!-- Vendor CSS Files -->
    <link href="../assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="../assets/vendor/bootstrap-icons/bootstrap-icons.css" rel="stylesheet">
    <link href="../assets/vendor/boxicons/css/boxicons.min.css" rel="stylesheet">
    <link href="../assets/vendor/quill/quill.snow.css" rel="stylesheet">
    <link href="../assets/vendor/quill/quill.bubble.css" rel="stylesheet">
    <link href="../assets/vendor/remixicon/remixicon.css" rel="stylesheet">
    <link href="../assets/vendor/simple-datatables/style.css" rel="stylesheet">

    <!-- Template Main CSS File -->
    <link href="../assets/css/style.css" rel="stylesheet">

</head>

<body>

<!-- ======= Header ======= -->
<header id="header" class="header fixed-top d-flex align-items-center">

    <div class="d-flex align-items-center justify-content-between">
        <a href="index.jsp" class="logo d-flex align-items-center">
            <img src="../assets/img/logo-ereno.png" alt="">
            <span class="d-none d-lg-block">ERENO-UI</span>
        </a>
        <i class="bi bi-list toggle-sidebar-btn"></i>
    </div><!-- End Logo -->

    <%--    <div class="search-bar">--%>
    <%--        <form class="search-form d-flex align-items-center" method="POST" action="#">--%>
    <%--            <input type="text" name="query" placeholder="Pesquisar configurações ou datasets"--%>
    <%--                   title="Enter search keyword">--%>
    <%--            <button type="submit" title="Pesquisar"><i class="bi bi-search"></i></button>--%>
    <%--        </form>--%>
    <%--    </div><!-- End Search Bar -->--%>

    <nav class="header-nav ms-auto">
        <ul class="d-flex align-items-center">

            <%--            <li class="nav-item d-block d-lg-none">--%>
            <%--                <a class="nav-link nav-icon search-bar-toggle " href="#">--%>
            <%--                    <i class="bi bi-search"></i>--%>
            <%--                </a>--%>
            <%--            </li><!-- End Search Icon-->--%>

            <%--            <li class="nav-item dropdown">--%>

            <%--                <a class="nav-link nav-icon" href="#" data-bs-toggle="dropdown">--%>
            <%--                    <i class="bi bi-bell"></i>--%>
            <%--                    <span class="badge bg-primary badge-number">4</span>--%>
            <%--                </a><!-- End Notification Icon -->--%>

            <%--                <ul class="dropdown-menu dropdown-menu-end dropdown-menu-arrow notifications">--%>
            <%--                    <li class="dropdown-header">--%>
            <%--                        You have 4 new notifications--%>
            <%--                        <a href="#"><span class="badge rounded-pill bg-primary p-2 ms-2">View all</span></a>--%>
            <%--                    </li>--%>
            <%--                    <li>--%>
            <%--                        <hr class="dropdown-divider">--%>
            <%--                    </li>--%>

            <%--                    <li class="notification-item">--%>
            <%--                        <i class="bi bi-exclamation-circle text-warning"></i>--%>
            <%--                        <div>--%>
            <%--                            <h4>Lorem Ipsum</h4>--%>
            <%--                            <p>Quae dolorem earum veritatis oditseno</p>--%>
            <%--                            <p>30 min. ago</p>--%>
            <%--                        </div>--%>
            <%--                    </li>--%>

            <%--                    <li>--%>
            <%--                        <hr class="dropdown-divider">--%>
            <%--                    </li>--%>

            <%--                    <li class="notification-item">--%>
            <%--                        <i class="bi bi-x-circle text-danger"></i>--%>
            <%--                        <div>--%>
            <%--                            <h4>Atque rerum nesciunt</h4>--%>
            <%--                            <p>Quae dolorem earum veritatis oditseno</p>--%>
            <%--                            <p>1 hr. ago</p>--%>
            <%--                        </div>--%>
            <%--                    </li>--%>

            <%--                    <li>--%>
            <%--                        <hr class="dropdown-divider">--%>
            <%--                    </li>--%>

            <%--                    <li class="notification-item">--%>
            <%--                        <i class="bi bi-check-circle text-success"></i>--%>
            <%--                        <div>--%>
            <%--                            <h4>Sit rerum fuga</h4>--%>
            <%--                            <p>Quae dolorem earum veritatis oditseno</p>--%>
            <%--                            <p>2 hrs. ago</p>--%>
            <%--                        </div>--%>
            <%--                    </li>--%>

            <%--                    <li>--%>
            <%--                        <hr class="dropdown-divider">--%>
            <%--                    </li>--%>

            <%--                    <li class="notification-item">--%>
            <%--                        <i class="bi bi-info-circle text-primary"></i>--%>
            <%--                        <div>--%>
            <%--                            <h4>Dicta reprehenderit</h4>--%>
            <%--                            <p>Quae dolorem earum veritatis oditseno</p>--%>
            <%--                            <p>4 hrs. ago</p>--%>
            <%--                        </div>--%>
            <%--                    </li>--%>

            <%--                    <li>--%>
            <%--                        <hr class="dropdown-divider">--%>
            <%--                    </li>--%>
            <%--                    <li class="dropdown-footer">--%>
            <%--                        <a href="#">Show all notifications</a>--%>
            <%--                    </li>--%>

            <%--                </ul><!-- End Notification Dropdown Items -->--%>

            <%--            </li><!-- End Notification Nav -->--%>


            <%--            <li class="nav-item dropdown pe-3">--%>

            <%--                <a class="nav-link nav-profile d-flex align-items-center pe-0" href="#" data-bs-toggle="dropdown">--%>
            <%--                    <img src="../assets/img/profile-img.jpg" alt="Profile" class="rounded-circle">--%>
            <%--                    <span class="d-none d-md-block dropdown-toggle ps-2">Silvio</span>--%>
            <%--                </a><!-- End Profile Iamge Icon -->--%>

            <%--                <ul class="dropdown-menu dropdown-menu-end dropdown-menu-arrow profile">--%>
            <%--                    <li class="dropdown-header">--%>
            <%--                        <h6>Silvio Quincozes</h6>--%>
            <%--                        <span>Web Designer</span>--%>
            <%--                    </li>--%>
            <%--                    <li>--%>
            <%--                        <hr class="dropdown-divider">--%>
            <%--                    </li>--%>

            <%--                    <li>--%>
            <%--                        <a class="dropdown-item d-flex align-items-center" href="users-profile.html">--%>
            <%--                            <i class="bi bi-person"></i>--%>
            <%--                            <span>My Profile</span>--%>
            <%--                        </a>--%>
            <%--                    </li>--%>
            <%--                    <li>--%>
            <%--                        <hr class="dropdown-divider">--%>
            <%--                    </li>--%>

            <%--                    <li>--%>
            <%--                        <a class="dropdown-item d-flex align-items-center" href="users-profile.html">--%>
            <%--                            <i class="bi bi-gear"></i>--%>
            <%--                            <span>Account Settings</span>--%>
            <%--                        </a>--%>
            <%--                    </li>--%>
            <%--                    <li>--%>
            <%--                        <hr class="dropdown-divider">--%>
            <%--                    </li>--%>

            <%--                    <li>--%>
            <%--                        <a class="dropdown-item d-flex align-items-center" href="pages-faq.html">--%>
            <%--                            <i class="bi bi-question-circle"></i>--%>
            <%--                            <span>Need Help?</span>--%>
            <%--                        </a>--%>
            <%--                    </li>--%>
            <%--                    <li>--%>
            <%--                        <hr class="dropdown-divider">--%>
            <%--                    </li>--%>

            <%--                    <li>--%>
            <%--                        <a class="dropdown-item d-flex align-items-center" href="#">--%>
            <%--                            <i class="bi bi-box-arrow-right"></i>--%>
            <%--                            <span>Sign Out</span>--%>
            <%--                        </a>--%>
            <%--                    </li>--%>

            <%--                </ul><!-- End Profile Dropdown Items -->--%>
            <%--            </li><!-- End Profile Nav -->--%>

            <li class="nav-item dropdown pe-3">

                <a class="nav-link nav-profile d-flex align-items-center pe-0" href="/ERENO_war_exploded/">
                    <img src="../assets/img/en-pt.png" alt="Language" class="rounded-circle">
                    <div style="display: none;"><%=Util.english = true%>
                    </div>

                </a>
            </li><!-- End Profile Nav -->

        </ul>
    </nav><!-- End Icons Navigation -->

</header><!-- End Header -->

<!-- ======= Sidebar ======= -->
<aside id="sidebar" class="sidebar">

    <ul class="sidebar-nav" id="sidebar-nav">

        <li class="nav-item">
            <a class="nav-link " data-bs-target="#forms-nav" data-bs-toggle="collapse" href="#">
                <i class="bi bi-journal-text"></i><span>Settings</span><i class="bi bi-chevron-down ms-auto"></i>
            </a>
            <ul id="forms-nav" class="nav-content expand " data-bs-parent="#sidebar-nav">
                <li>
                    <a href="ied-configs.jsp">
                        <i class="bi bi-circle"></i><span style="color: #06c !important">IED Settings</span>
                    </a>
                    <a href="goose-message.jsp">
                        <i class="bi bi-circle"></i><span>GOOSE Flow</span>
                    </a>
                    <a href="upload-samples.jsp">
                        <i class="bi bi-circle"></i><span>SV Data Upload</span>
                    </a>
                    <a href="attack-definitions.jsp">
                        <i class="bi bi-circle"></i><span>Attacks</span>
                    </a>
                </li>
            </ul>
        </li><!-- End Forms Nav -->

        <li class="nav-item">
            <a class="nav-link collapsed" href="download-datasets.jsp">
                <i class="bi bi-download"></i>
                <span>Datasets</span>
            </a>
        </li><!-- End Dashboard Nav -->
    </ul>
</aside><!-- End Sidebar-->

<main id="main" class="main">

    <div class="pagetitle">
        <h1>Settings</h1>
        <nav>
            <ol class="breadcrumb">
                <li class="breadcrumb-item" active><a style="color: #06c !important">IED Settings</a></li>
                <li class="breadcrumb-item"> GOOSE Flow</li>
                <li class="breadcrumb-item"> SV Data Upload</li>
                <li class="breadcrumb-item"> Attacks</li>
                <li class="breadcrumb-item"> Dataset Download</li>
            </ol>
        </nav>
    </div><!-- End Page Title -->

    <section class="section">
        <div class="row">
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">IED Settings</h5>


                        <!-- General Form Elements -->
                        <form action="../setup-ied" method="post" accept-charset="utf-8">
                            <%
                                // Loading saved values to update form
                                SetupIED.ECF.loadConfigs(application);
                            %>
                            <!-- Floating Labels Form -->
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="text" class="form-control" id="iedName" name="iedName"
                                               placeholder="Protection IED" value="<%=SetupIED.ECF.iedName%>">
                                        <label for="iedName">IED Name</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="text" class="form-control" id="gocbRef" name="gocbRef"
                                               placeholder="LD/LLN0$GO$gcbA" value="<%=SetupIED.ECF.gocbRef%>">
                                        <label for="gocbRef">Control Block (gocbRef)</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="text" class="form-control" id="datSet" name="datSet"
                                               placeholder="LD/LLN0$GO$gcbA" value="<%=SetupIED.ECF.datSet%>">
                                        <label for="datSet">DatSet Path</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="number" class="form-control" id="minTime" name="minTime"
                                               placeholder="4" value="<%=SetupIED.ECF.minTime%>">
                                        <label for="maxTime">Minimum Transmission Interval (MinTime)</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="number" class="form-control" id="maxTime" name="maxTime"
                                               placeholder="1000" value="<%=SetupIED.ECF.maxTime%>">
                                        <label for="maxTime">Maximum Transmission Interval (MaxTime)</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="number" class="form-control" id="timestamp" name="timestamp"
                                               placeholder="0" value="<%=SetupIED.ECF.timestamp%>">
                                        <label for="stNum">Initial Timestamp (t)</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="number" class="form-control" id="stNum" name="stNum"
                                               placeholder="0" value="<%=SetupIED.ECF.stNum%>">
                                        <label for="stNum">Initial Status Number (StNum)</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="number" class="form-control" id="sqNum" name="sqNum"
                                               placeholder="0" value="<%=SetupIED.ECF.sqNum%>">
                                        <label for="sqNum">Initial Sequence Number (SqNum)</label>
                                    </div>
                                </div>

                                <div>
                                    <button type="reset" class="btn btn-secondary">Back</button>
                                    <%--                        <button type="submit" class="btn btn-primary"><a style="color:white ;" href="goose-message.jsp">Próximo</a>--%>
                                    <button type="submit" class="btn btn-primary">Next</button>
                                    </button>
                                </div>

                            </div>
                        </form>
                    </div>

                </div><!-- End General Form Elements -->
            </div>
        </div>
    </section>

</main><!-- End #main -->

<!-- ======= Footer ======= -->
<footer id="footer" class="footer">
    <div class="copyright">
        Copyright &copy; 2022 <a href="https://github.com/sequincozes/ereno">ERENO</a>
        <br>
        All rights reserved.
    </div>
</footer><!-- End Footer -->

<a href="#" class="back-to-top d-flex align-items-center justify-content-center"><i
        class="bi bi-arrow-up-short"></i></a>

<!-- Vendor JS Files -->
<script src="../assets/vendor/apexcharts/apexcharts.min.js"></script>
<script src="../assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="../assets/vendor/chart.js/chart.min.js"></script>
<script src="../assets/vendor/echarts/echarts.min.js"></script>
<script src="../assets/vendor/quill/quill.min.js"></script>
<script src="../assets/vendor/simple-datatables/simple-datatables.js"></script>
<script src="../assets/vendor/tinymce/tinymce.min.js"></script>
<script src="../assets/vendor/php-email-form/validate.js"></script>

<!-- Template Main JS File -->
<script src="../assets/js/main.js"></script>

</body>

</html>