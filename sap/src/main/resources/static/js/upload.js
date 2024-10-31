// 업로드 폼 전송 이벤트 처리
document.getElementById("uploadForm").onsubmit = async function(event) {
    event.preventDefault();
    const formData = new FormData(this);
    const messageElement = document.getElementById("message");

    try {
        // 파일 업로드 요청 전송
        const response = await fetch("/api/items/upload", {
            method: "POST",
            body: formData
        });

        // 응답 처리
        const result = await response.text();
        messageElement.innerText = response.ok ? "파일이 성공적으로 업로드되었습니다!" : "오류 발생: " + result;
        messageElement.style.color = response.ok ? "#4CAF50" : "#e74c3c"; // 성공 시 녹색, 오류 시 빨간색
    } catch (error) {
        messageElement.innerText = "파일 업로드 중 문제가 발생했습니다.";
        messageElement.style.color = "#e74c3c";
    }
};
