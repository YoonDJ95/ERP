// 최근 검색어 저장 배열
const recentItems = [];

// 폼 제출 시 유효성 검사 및 fetch 요청
document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.transaction-form').addEventListener('submit', function(event) {
        event.preventDefault(); // 기본 제출 방지

        // 입력 필드 유효성 검사
        const itemId = document.getElementById('itemId').value;
        const transactionDate = document.getElementById('transactionDate').value;
        const transactionType = document.getElementById('transactionType').value;
        const quantity = document.getElementById('quantity').value;

        // 필드가 비어 있는지 확인
        if (!itemId || !transactionDate || !transactionType || !quantity) {
            document.getElementById('message').textContent = '모든 필드를 채워주세요.';
            document.getElementById('message').style.color = 'red';
            return;
        }

        // 제품명이 올바른지 확인
        if (!isItemValid(itemId)) {
            document.getElementById('message').textContent = '유효한 제품명을 입력해주세요.';
            document.getElementById('message').style.color = 'red';
            return;
        }

        // 유효성 검사를 통과한 경우 fetch로 서버에 데이터 전송
        const formData = {
            itemId: itemId,
            transactionDate: transactionDate,
            transactionType: transactionType,
            quantity: quantity
        };

		fetch('/addTransaction', {
		    method: 'POST',
		    headers: {
		        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
		    },
		    body: new URLSearchParams(formData)
		})
		.then(response => {
		    if (!response.ok) {
		        return response.json().then(error => {
		            throw new Error(error.message || '서버 오류로 인해 거래 등록에 실패했습니다.');
		        });
		    }
		    return response.json();
		})
		.then(data => {
		    const messageElement = document.getElementById('message');
		    console.log("응답 데이터:", data);  // 응답 데이터 확인

		    // 메시지와 스타일 업데이트
		    messageElement.style.display = 'block';
		    messageElement.style.color = 'green';
		    messageElement.innerHTML = data.message + " 등록완료";  // innerHTML로 설정

		    // 강제 렌더링
		    requestAnimationFrame(() => {
		        messageElement.textContent = data.message + " 등록완료";  // textContent로 한 번 더 설정
		    });

		    clearForm(); // 폼 초기화
		})
		.catch(error => {
		    const messageElement = document.getElementById('message');
		    messageElement.style.display = 'block';
		    messageElement.style.color = 'red';
		    messageElement.innerHTML = error.message;
		    console.error("Fetch 요청 실패:", error);
		});

    });

    // 폼 초기화 함수
    function clearForm() {
        document.querySelector(".transaction-form").reset();
        document.getElementById('suggestions').style.display = 'none';
        document.getElementById('message').textContent = "";
    }

    // 유효한 제품명인지 확인 (예: recentItems 배열에서 확인)
    function isItemValid(itemId) {
        return recentItems.includes(itemId);
    }
});

// 최근 검색어 목록 표시
function showRecentItems() {
    const suggestions = document.getElementById('suggestions');
    suggestions.innerHTML = '';
    recentItems.slice(-5).reverse().forEach(item => {
        const listItem = document.createElement('li');
        listItem.textContent = item;
        listItem.onclick = () => selectItem(item);
        suggestions.appendChild(listItem);
    });
    suggestions.style.display = 'block';
}

// 서버에서 아이템 검색
function searchItems() {
    const keyword = document.getElementById('itemId').value;
    fetch('http://localhost:8080/api/items/searchItems', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
        },
        body: new URLSearchParams({ keyword: keyword })
    })
    .then(response => response.json())
    .then(items => {
        const suggestions = document.getElementById('suggestions');
        suggestions.innerHTML = '';
        items.forEach(item => {
            const listItem = document.createElement('li');
            listItem.textContent = item.name;
            listItem.onclick = () => selectItem(item.name);
            suggestions.appendChild(listItem);
        });
        suggestions.style.display = 'block';
    })
    .catch(error => {
        console.error("Fetch 요청 실패:", error);
    });
}

// 제품명 선택 시 ID 입력 필드에 설정 및 목록 숨김
function selectItem(name) {
    document.getElementById('itemId').value = name;
    recentItems.push(name);
    document.getElementById('suggestions').style.display = 'none';
}

// 검색 목록 외 클릭 시 목록 숨기기
document.addEventListener('click', function(e) {
    if (!e.target.closest('#itemId, #suggestions')) {
        document.getElementById('suggestions').style.display = 'none';
    }
});
