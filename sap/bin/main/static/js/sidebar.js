// 사이드바 토글 함수
function toggleSidebar() {
	const sidebar = document.getElementById('sidebar');
	const toggleIcon = document.getElementById('toggle-icon');
	const toggleBtn = document.getElementById('toggle-btn');
	sidebar.classList.toggle('open');

	// 사이드바 상태에 따라 버튼 위치 조정
	if (sidebar.classList.contains('open')) {
		toggleIcon.innerHTML = '&lt;'; // 닫기 아이콘 '<'
		toggleBtn.style.left = '182px'; // 사이드바가 열린 상태에서의 위치
	} else {
		toggleIcon.innerHTML = '&gt;'; // 열기 아이콘 '>'
		toggleBtn.style.left = '42px'; // 닫힌 상태에서의 위치
	}
}

				// 탭 클릭 이벤트
$(document).ready(function(){
	$('ul.tabs li').click(function(){
		var tab_id = $(this).attr('data-tab');

	$('ul.tabs li').removeClass('current');
	$('.tab-content').removeClass('current');

	$(this).addClass('current');
	$("#" + tab_id).addClass('current');
	});
});