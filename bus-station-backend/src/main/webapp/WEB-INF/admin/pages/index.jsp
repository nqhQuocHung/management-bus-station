<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Roles and Companies Chart</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <style>
        #chartContainer {
            width: 60%;
            height: 40%;
            margin: auto;
        }
    </style>
</head>
<body>
<h2 style="text-align: center">Statistical data system</h2>

<div id="chartContainer">
    <canvas id="roleChart" width="400" height="200"></canvas>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const ctx = document.getElementById('roleChart').getContext('2d');

        const fetchData = async () => {
            const roles = ['Customer', 'Admin', 'Manager', 'Companies'];
            const data = [];

            try {
                const customerResponse = await axios.get(`http://localhost:8080/busstation/api/v1/transportation_company/count/user/1`);
                const adminResponse = await axios.get(`http://localhost:8080/busstation/api/v1/transportation_company/count/user/2`);
                const managerResponse = await axios.get(`http://localhost:8080/busstation/api/v1/transportation_company/count/user/3`);

                data.push(customerResponse.data);
                data.push(adminResponse.data);
                data.push(managerResponse.data);

                const companyResponse = await axios.get(`http://localhost:8080/busstation/api/v1/transportation_company/count/company`);
                data.push(companyResponse.data);

                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: roles,
                        datasets: [{
                            label: 'Statistics',
                            data: data,
                            backgroundColor: [
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(75, 192, 192, 0.2)'
                            ],
                            borderColor: [
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(75, 192, 192, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
            } catch (error) {
                console.error(error.message);
            }
        };

        fetchData();
    });
</script>
</body>
</html>
