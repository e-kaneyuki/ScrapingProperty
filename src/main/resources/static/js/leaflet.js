/**
 * 
 */
window.addEventListener("load", function() {
	
    // 地図を表示する要素の数だけ繰り返し
    var elements = document.querySelectorAll(".map");
    elements.forEach(function(e, index) {
        var latitude = e.getAttribute('data-latitude');
        var longitude = e.getAttribute('data-longitude');
        var shopName = e.getAttribute('data-store-name');

		// 文字列から数値に変換
        var lat = parseFloat(latitude); 
        var lng = parseFloat(longitude); 
        

        var map = L.map(e.id).setView([lat, lng], 10);
        
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: "© OpenStreetMap contributors"
        }).addTo(map);
		
		

		
		
        // マーカーを追加
        var marker = L.marker([lat, lng]).addTo(map);
        marker.bindPopup(shopName).openPopup(); 
    });
    alert('表示しました');
});