   // Configuração do cliente WebSocket
    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {

    stompClient.subscribe('/topic/analysisResult', function(message) {
        var eventData = JSON.parse(message.body);

        document.getElementById('result-container').innerText = getEmotionAnalysis(JSON.stringify(eventData));
        document.getElementById('loading-container').style.display = 'none';
        document.getElementById('result-container').style.display = 'block';
    });
});

  function getEmotionAnalysis(result) {
      var likelihoodMap = {
    VERY_UNLIKELY: "Very Unlikely",
    UNLIKELY: "Unlike",
    POSSIBLE: "Possible",
    LIKELY: "Likely",
    VERY_LIKELY: "Very Likely"
};
      var analise = JSON.parse(result);
      var joyMessage = likelihoodMap[analise[0].joyLikelihood];
      var angerMessage = likelihoodMap[analise[0].angerLikelihood];
      var sorrowMessage = likelihoodMap[analise[0].sorrowLikelihood];
      var surpriseMessage = likelihoodMap[analise[0].surpriseLikelihood];

      var message = `
        Joy: ${joyMessage}\n
        Anger: ${angerMessage}\n
        Sorrow: ${sorrowMessage}\n
        Surprise: ${surpriseMessage}
    `;

    return message;
}

   $(document).ready(function() {
       $('.send-coordinates').click(function() {
           var latitude = $(this).data('latitude');
           var longitude = $(this).data('longitude');
           $.ajax({
               type: 'POST',
               url: '/maps',
               data: {
                   latitude: latitude,
                   longitude: longitude
               },
               success: function(response) {
               },
               error: function(xhr, status, error) {
               }
           });
       });
   });
