// 아이템 목록을 가져와 테이블에 추가하는 스크립트
fetch('/api/items/list')
    .then(response => response.json())
    .then(items => {
        const tableBody = document.getElementById("item-table-body");
        items.forEach(item => {
            // 각 아이템의 데이터를 테이블 행에 추가
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${item.id}</td>
                <td>${item.name}</td>
                <td>${item.parts}</td>
                <td>${item.maker}</td>
                <td>${item.purchasePrice}</td>
                <td>${item.sellPrice}</td>
                <td>${item.performance}</td>
            `;
            tableBody.appendChild(row);
        });
    })
    .catch(error => console.error('아이템 목록을 가져오는 중 오류:', error));
