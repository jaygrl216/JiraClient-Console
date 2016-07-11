var ctx = document.getElementById("chart");

var data = {
    labels: [
        "To Do",
        "Completed"
    ],
    datasets: [
        {
            data: [20, 80],
            backgroundColor: [
                "#FF6384",
                "#36A2EB",
            ],
            hoverBackgroundColor: [
                "#FF6384",
                "#36A2EB",
            ]
        }]
};

var myPieChart = new Chart(ctx,{
    type: 'pie',
    data: data,
});