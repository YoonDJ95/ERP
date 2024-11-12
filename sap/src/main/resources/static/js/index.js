// 1. 아이템 목록을 가져오기
fetch('/api/items/list')
    .then(response => response.json())
    .then(items => {
        // 2. 아이템 데이터를 차트에 필요한 형태로 변환
        const salesData = items.map(item => item.sellPrice); // 판매 가격
        const purchaseData = items.map(item => item.purchasePrice); // 구매 가격
        const performanceData = items.map(item => item.performance); // 성능
        const itemNames = items.map(item => item.name); // 아이템 이름

      

        // 원형 차트
        fetch('/getPieChartData')
            .then(response => response.json())
            .then(data => {
                const ctxPie = document.getElementById('pieChart').getContext('2d');
                const pieChart = new Chart(ctxPie, {
                    type: 'pie',
                    data: {
                        labels: data.labels, // parts 이름이 범례로 표시
                        datasets: [{
                            label: '아이템 성능 비율',
                            data: data.data, // 판매 수량 데이터
                            backgroundColor: [
                                'rgba(255, 99, 132, 0.6)',
                                'rgba(54, 162, 235, 0.6)',
                                'rgba(75, 192, 192, 0.6)',
                                'rgba(255, 206, 86, 0.6)',
                                'rgba(153, 102, 255, 0.6)',
                                'rgba(255, 159, 64, 0.6)',
                                'rgba(199, 199, 199, 0.6)',
                                'rgba(83, 102, 122, 0.6)',
                                'rgba(205, 130, 158, 0.6)',
                                'rgba(255, 140, 102, 0.6)',
                                'rgba(46, 139, 87, 0.6)',
                                'rgba(72, 209, 204, 0.6)',
                                'rgba(100, 149, 237, 0.6)',
                                'rgba(218, 165, 32, 0.6)',
                                'rgba(255, 69, 0, 0.6)',
                                'rgba(138, 43, 226, 0.6)',
                                'rgba(210, 105, 30, 0.6)',
                                'rgba(147, 112, 219, 0.6)'
                            ],
                            borderColor: 'rgba(255, 255, 255, 1)',
                            borderWidth: 2
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { position: 'top' },
                            tooltip: {
                                callbacks: {
                                    label: function(tooltipItem) {
                                        return tooltipItem.label + ': ' + tooltipItem.raw;
                                    }
                                }
                            }
                        }
                    }
                });
            })
            .catch(error => console.error('Error:', error));
    })
    .catch(error => console.error('아이템 목록을 가져오는 중 오류:', error));

	// 월별 수익 차트
	fetch('/getMonthlyProfitData')
	    .then(response => response.json())
	    .then(data => {
	        // 월별 한글 이름 배열
	        const monthNames = [
	            '1월', '2월', '3월', '4월', '5월', '6월',
	            '7월', '8월', '9월', '10월', '11월', '12월'
	        ];

	        // 데이터에서 월별 수익과 해당 월의 한글 이름을 매핑
	        const months = data.map(item => monthNames[item.month - 1]);  // item.month는 1~12 범위
	        const profits = data.map(item => item.totalProfit);

	        const ctxMonthlyProfit = document.getElementById('monthlyProfitChart').getContext('2d');
	        const monthlyProfitChart = new Chart(ctxMonthlyProfit, {
	            type: 'line',
	            data: {
	                labels: months,  // '1월', '2월' 형식으로 표시됨
	                datasets: [{
	                    label: '월별 수익',
	                    data: profits,
	                    fill: false,
	                    borderColor: 'rgba(75, 192, 192, 1)',
	                    tension: 0.1
	                }]
	            },
	            options: {
	                scales: {
	                    y: { beginAtZero: true }
	                }
	            }
	        });
	    })
	    .catch(error => console.error('월별 수익 데이터를 불러오는 중 오류:', error));
		

// 상단 데이터값을 불러옴
function formatCurrency(value) {
    return '₩' + new Intl.NumberFormat('ko-KR', { minimumFractionDigits: 0 }).format(value);
}

// 상위 판매 품목 가져와서 표시하기
fetch("/top-selling-items")
    .then(response => response.json())
    .then(data => {
        const tableBody = document.querySelector("#top-selling-items-table tbody");
        tableBody.innerHTML = '';
        data.forEach(item => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${item['부품명']}</td>
                <td>${item['제품명']}</td>
                <td>${item['제조사']}</td>
                <td>${formatCurrency(item['매입가격'])}</td>
                <td>${formatCurrency(item['판매가격'])}</td>
                <td>${item['판매량']}</td>
            `;
            tableBody.appendChild(row);
        });
    })
    .catch(error => console.error("상위 판매 품목을 불러오는 중 오류:", error));

// 상단 요약 데이터 가져와서 원화 형식으로 출력하기
fetch("/getSummaryTilesData")
    .then(response => response.json())
    .then(data => {
        document.getElementById('newOrders').textContent = data.newOrders;
        document.getElementById('dailyProfit').textContent = formatCurrency(data.dailyProfit);
        document.getElementById('inventoryValue').textContent = formatCurrency(data.inventoryValue);
        document.getElementById('inventoryCount').textContent = data.inventoryCount;
    })
    .catch(error => console.error('요약 데이터를 불러오는 중 오류:', error));