// 부품별 제조사 목록을 저장할 객체
let partsToMakers = {};
let allItems = [];

// 페이지 로드 시 API에서 아이템 목록을 가져와 테이블을 초기화하고 필터링에 사용할 데이터 준비
fetch('/api/items/list')
    .then(response => response.json())
    .then(items => {
        allItems = items;
        const tableBody = document.getElementById("item-table-body");

        // 부품별 제조사 목록 생성 및 테이블 초기 데이터 삽입
        items.forEach(item => {
            if (!partsToMakers[item.parts]) {
                partsToMakers[item.parts] = new Set();
            }
            partsToMakers[item.parts].add(item.maker);

            // 가격을 천 단위 구분 기호로 포맷팅 (화폐 기호 제거)
            const formattedPurchasePrice = new Intl.NumberFormat('ko-KR').format(item.purchasePrice);
            const formattedSellPrice = new Intl.NumberFormat('ko-KR').format(item.sellPrice);

            // 초기 테이블 표시
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${item.id}</td>
                <td>${item.name}</td>
                <td>${item.parts}</td>
                <td>${item.maker}</td>
                <td>${formattedPurchasePrice}</td>
                <td>${formattedSellPrice}</td>
                <td>${item.performance}</td>
            `;
            tableBody.appendChild(row);
        });

        // 부품 드롭다운 설정
        const partsSelect = document.getElementById("parts");
        Object.keys(partsToMakers).forEach(part => {
            const option = document.createElement("option");
            option.value = part;
            option.textContent = part.toUpperCase();
            partsSelect.appendChild(option);
        });
    })
    .catch(error => console.error('아이템 목록을 가져오는 중 오류:', error));

// 필터링된 결과로 테이블 업데이트
function updateTable(items) {
    const tableBody = document.getElementById("item-table-body");
    tableBody.innerHTML = ""; // 기존 테이블 초기화

    items.forEach(item => {
        const formattedPurchasePrice = new Intl.NumberFormat('ko-KR').format(item.purchasePrice);
        const formattedSellPrice = new Intl.NumberFormat('ko-KR').format(item.sellPrice);

        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${item.id}</td>
            <td>${item.name}</td>
            <td>${item.parts}</td>
            <td>${item.maker}</td>
            <td>${formattedPurchasePrice}</td>
            <td>${formattedSellPrice}</td>
            <td>${item.performance}</td>
        `;
        tableBody.appendChild(row);
    });
}
