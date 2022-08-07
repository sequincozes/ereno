<%@ page import="br.ufu.facom.ereno.api.Attacks" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">

    <title>Configurações - ERENO</title>
    <meta content="" name="description">
    <meta content="" name="keywords">

    <!-- Favicons -->
    <link href="assets/img/favicon.png" rel="icon">
    <link href="assets/img/apple-touch-icon.png" rel="apple-touch-icon">

    <!-- Google Fonts -->
    <link href="https://fonts.gstatic.com" rel="preconnect">
    <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,600,600i,700,700i|Nunito:300,300i,400,400i,600,600i,700,700i|Poppins:300,300i,400,400i,500,500i,600,600i,700,700i"
          rel="stylesheet">

    <!-- Vendor CSS Files -->
    <link href="assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/vendor/bootstrap-icons/bootstrap-icons.css" rel="stylesheet">
    <link href="assets/vendor/boxicons/css/boxicons.min.css" rel="stylesheet">
    <link href="assets/vendor/quill/quill.snow.css" rel="stylesheet">
    <link href="assets/vendor/quill/quill.bubble.css" rel="stylesheet">
    <link href="assets/vendor/remixicon/remixicon.css" rel="stylesheet">
    <link href="assets/vendor/simple-datatables/style.css" rel="stylesheet">

    <!-- Template Main CSS File -->
    <link href="assets/css/style.css" rel="stylesheet">

</head>

<body>

<!-- ======= Header ======= -->
<header id="header" class="header fixed-top d-flex align-items-center">

    <div class="d-flex align-items-center justify-content-between">
        <a href="index.jsp" class="logo d-flex align-items-center">
            <img src="assets/img/logo-ereno.png" alt="">
            <span class="d-none d-lg-block">ERENO-UI</span>
        </a>
        <i class="bi bi-list toggle-sidebar-btn"></i>
    </div><!-- End Logo -->

    <div class="search-bar">
        <form class="search-form d-flex align-items-center" method="POST" action="#">
            <input type="text" name="query" placeholder="Pesquisar configurações ou datasets"
                   title="Enter search keyword">
            <button type="submit" title="Pesquisar"><i class="bi bi-search"></i></button>
        </form>
    </div><!-- End Search Bar -->

    <nav class="header-nav ms-auto">
        <ul class="d-flex align-items-center">

            <li class="nav-item d-block d-lg-none">
                <a class="nav-link nav-icon search-bar-toggle " href="#">
                    <i class="bi bi-search"></i>
                </a>
            </li><!-- End Search Icon-->

            <li class="nav-item dropdown">

                <a class="nav-link nav-icon" href="#" data-bs-toggle="dropdown">
                    <i class="bi bi-bell"></i>
                    <span class="badge bg-primary badge-number">4</span>
                </a><!-- End Notification Icon -->

                <ul class="dropdown-menu dropdown-menu-end dropdown-menu-arrow notifications">
                    <li class="dropdown-header">
                        You have 4 new notifications
                        <a href="#"><span class="badge rounded-pill bg-primary p-2 ms-2">View all</span></a>
                    </li>
                    <li>
                        <hr class="dropdown-divider">
                    </li>

                    <li class="notification-item">
                        <i class="bi bi-exclamation-circle text-warning"></i>
                        <div>
                            <h4>Lorem Ipsum</h4>
                            <p>Quae dolorem earum veritatis oditseno</p>
                            <p>30 min. ago</p>
                        </div>
                    </li>

                    <li>
                        <hr class="dropdown-divider">
                    </li>

                    <li class="notification-item">
                        <i class="bi bi-x-circle text-danger"></i>
                        <div>
                            <h4>Atque rerum nesciunt</h4>
                            <p>Quae dolorem earum veritatis oditseno</p>
                            <p>1 hr. ago</p>
                        </div>
                    </li>

                    <li>
                        <hr class="dropdown-divider">
                    </li>

                    <li class="notification-item">
                        <i class="bi bi-check-circle text-success"></i>
                        <div>
                            <h4>Sit rerum fuga</h4>
                            <p>Quae dolorem earum veritatis oditseno</p>
                            <p>2 hrs. ago</p>
                        </div>
                    </li>

                    <li>
                        <hr class="dropdown-divider">
                    </li>

                    <li class="notification-item">
                        <i class="bi bi-info-circle text-primary"></i>
                        <div>
                            <h4>Dicta reprehenderit</h4>
                            <p>Quae dolorem earum veritatis oditseno</p>
                            <p>4 hrs. ago</p>
                        </div>
                    </li>

                    <li>
                        <hr class="dropdown-divider">
                    </li>
                    <li class="dropdown-footer">
                        <a href="#">Show all notifications</a>
                    </li>

                </ul><!-- End Notification Dropdown Items -->

            </li><!-- End Notification Nav -->


            <li class="nav-item dropdown pe-3">

                <a class="nav-link nav-profile d-flex align-items-center pe-0" href="#" data-bs-toggle="dropdown">
                    <img src="assets/img/profile-img.jpg" alt="Profile" class="rounded-circle">
                    <span class="d-none d-md-block dropdown-toggle ps-2">Silvio</span>
                </a><!-- End Profile Iamge Icon -->

                <ul class="dropdown-menu dropdown-menu-end dropdown-menu-arrow profile">
                    <li class="dropdown-header">
                        <h6>Silvio Quincozes</h6>
                        <span>Web Designer</span>
                    </li>
                    <li>
                        <hr class="dropdown-divider">
                    </li>

                    <li>
                        <a class="dropdown-item d-flex align-items-center" href="users-profile.html">
                            <i class="bi bi-person"></i>
                            <span>My Profile</span>
                        </a>
                    </li>
                    <li>
                        <hr class="dropdown-divider">
                    </li>

                    <li>
                        <a class="dropdown-item d-flex align-items-center" href="users-profile.html">
                            <i class="bi bi-gear"></i>
                            <span>Account Settings</span>
                        </a>
                    </li>
                    <li>
                        <hr class="dropdown-divider">
                    </li>

                    <li>
                        <a class="dropdown-item d-flex align-items-center" href="pages-faq.html">
                            <i class="bi bi-question-circle"></i>
                            <span>Need Help?</span>
                        </a>
                    </li>
                    <li>
                        <hr class="dropdown-divider">
                    </li>

                    <li>
                        <a class="dropdown-item d-flex align-items-center" href="#">
                            <i class="bi bi-box-arrow-right"></i>
                            <span>Sign Out</span>
                        </a>
                    </li>

                </ul><!-- End Profile Dropdown Items -->
            </li><!-- End Profile Nav -->

        </ul>
    </nav><!-- End Icons Navigation -->

</header><!-- End Header -->

<!-- ======= Sidebar ======= -->
<aside id="sidebar" class="sidebar">

    <ul class="sidebar-nav" id="sidebar-nav">

        <li class="nav-item">
            <a class="nav-link " data-bs-target="#forms-nav" data-bs-toggle="collapse" href="#">
                <i class="bi bi-journal-text"></i><span>Configurações</span><i class="bi bi-chevron-down ms-auto"></i>
            </a>
            <ul id="forms-nav" class="nav-content expand " data-bs-parent="#sidebar-nav">
                <li>
                    <a href="ied-configs.jsp">
                        <i class="bi bi-circle"></i><span>Configurações do Ambiente</span>
                    </a>
                    <a href="goose-message.jsp">
                        <i class="bi bi-circle"></i><span>Fluxos de Mensagens</span>
                    </a>
                    <a href="upload-samples.jsp">
                        <i class="bi bi-circle"></i><span>Upload de Leituras</span>
                    </a>
                    <a href="attack-definitions.jsp">
                        <i class="bi bi-circle"></i><span style="color: #06c !important">Ataques</span>
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
        <h1>Configurações do Ambiente</h1>
        <nav>
            <ol class="breadcrumb">
                <li class="breadcrumb-item">Configurações do IED</li>
                <li class="breadcrumb-item"> Fluxo de Mensagens</li>
                <li class="breadcrumb-item"> Upload de Leituras</li>
                <li class="breadcrumb-item" active><a style="color: #06c"> Ataques </a></li>
                <li class="breadcrumb-item"> Download do Dataset</li>
            </ol>
        </nav>
    </div><!-- End Page Title -->

    <section class="section">
        <div class="row">
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Configurações do IED</h5>

                        <!-- General Form Elements -->
                        <form action="attacks" method="post" accept-charset="utf-8">
                            <%
                                // Loading saved values to update form
                                Attacks attacks = new Attacks();
                                attacks.loadConfigs(application.getRealPath("ecf/attacks.json"));
                            %>

                            <!-- Floating Labels Form -->
                            <div class="row g-3">
                                <div class="col-md-12">
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="legitimate"
                                                   name="legitimate" <%=isChecked(attacks.isLegitimate())%>>
                                            <label class="form-check-label" for="legitimate">Incluir mensagens
                                                legítimas</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="random-replay"
                                                   name="random-replay" <%=isChecked(attacks.isRandomReplay())%>
                                            <label class="form-check-label" for="random-replay"> Ataques de replay
                                                aleatório</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="inverse-replay"
                                                   name="inverse-replay" <%=isChecked(attacks.isInverseReplay())%>
                                            <label class="form-check-label" for="inverse-replay"> Ataques de replay
                                                inverso</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="masquerade-outage"
                                                   name="masquerade-outage" <%=isChecked(attacks.isMasqueradeOutage())%>
                                            <label class="form-check-label" for="masquerade-outage"> Ataques masquerade
                                                (queda de energia)</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="masquerade-damage"
                                                   name="masquerade-damage" <%=isChecked(attacks.isMasqueradeDamage())%>
                                            <label class="form-check-label" for="masquerade-damage"> Ataques masquerade
                                                (dano ao equipamento)</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="random-injection"
                                                   name="random-injection" <%=isChecked(attacks.isRandomInjection())%>
                                            <label class="form-check-label" for="random-injection"> Ataques de injeção
                                                de mensagens (aleatório)</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="high-st-num"
                                                   name="high-st-num" <%=isChecked(attacks.isHighStNum())%>>
                                            <label class="form-check-label" for="high-st-num"> Ataques DoS
                                                (High-StNum)</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="flooding"
                                                   name="flooding" <%=isChecked(attacks.isFlooding())%>>
                                            <label class="form-check-label" for="flooding"> Ataques DoS
                                                (Flooding)</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div>
                                <br>
                                <button type="reset" class="btn btn-secondary"><a style="color:white ;"
                                                                                  href="upload-samples.jsp">Voltar</a>
                                </button>
                                <button type="submit" class="btn btn-primary">Finalizar</button>
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
        Todos os direitos reservados.
    </div>
</footer><!-- End Footer -->

<a href="#" class="back-to-top d-flex align-items-center justify-content-center"><i
        class="bi bi-arrow-up-short"></i></a>

<!-- Vendor JS Files -->
<script src="assets/vendor/apexcharts/apexcharts.min.js"></script>
<script src="assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="assets/vendor/chart.js/chart.min.js"></script>
<script src="assets/vendor/echarts/echarts.min.js"></script>
<script src="assets/vendor/quill/quill.min.js"></script>
<script src="assets/vendor/simple-datatables/simple-datatables.js"></script>
<script src="assets/vendor/tinymce/tinymce.min.js"></script>
<script src="assets/vendor/php-email-form/validate.js"></script>

<!-- Template Main JS File -->
<script src="assets/js/main.js"></script>

</body>

</html>
<%!
    private String isChecked(boolean isChecked) {
        if (isChecked) {
            return "checked";
        } else {
            return "";
        }
    }
%>