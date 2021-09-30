var EventDAO = (function() {
    var resourcePath = "rest/event/";
    var requestByAjax = function(data, done, fail, always) {
	done = typeof done !== 'undefined' ? done : function() {};
	fail = typeof fail !== 'undefined' ? fail : function() {};
	always = typeof always !== 'undefined' ? always : function() {};

	let authToken = localStorage.getItem('authorization-token');
		if (authToken !== null) {
		    data.beforeSend = function(xhr) {
			xhr.setRequestHeader('Authorization', 'Basic ' + authToken);
		    };
		}
	
		$.ajax(data).done(done).fail(fail).always(always);
	    };
	
	    function EventDAO() {
		

		this.listNormalEvents = function(pag,done, fail, always) {
            requestByAjax({
            url : resourcePath+"?type=normal&pag="+pag,
                        async: false,
            type : 'GET',
                        data: pag
            }, done, fail, always);
        };

        this.listFilterNormalEvents = function(pag,txtToFilt,done, fail, always) {
            requestByAjax({
             url : resourcePath+"?type=filterNormal&pag="+pag+"&txtToFilt="+txtToFilt,
                        async: false,
            type : 'GET',
                        data: pag
            }, done, fail, always);
        };

        this.listImportantEvents = function(done, fail, always) {
            requestByAjax({
            url : resourcePath+"?type=important",
                        async: false,
            type : 'GET'
            }, done, fail, always);
        };

        this.numNormalEvents = function(done, fail, always) {
            requestByAjax({
             url : resourcePath+"?type=numNormal",
                        async: false,
            type : 'GET'
            }, done, fail, always);
        };

        this.numFilterNormalEvents = function(txtToFilt,done, fail, always) {
            requestByAjax({
             url : resourcePath+"?type=numFilterNormal&txtToFilt="+txtToFilt,
                        async: false,
            type : 'GET'
            }, done, fail, always);
        };

        this.isAssistantOfEvent = function(idEvento,idUsuario,done, fail, always) {
            requestByAjax({
            url : resourcePath+"?type=isAssistantOfEvent&userID="+idUsuario+"&eventID="+idEvento,
             async: false,
            type : 'GET'
            }, done, fail, always);
        };
        
        this.numUsersInEvent = function(idEvento,done, fail, always) {
            requestByAjax({
            url : resourcePath+"?type=numUsersInEvent&eventID="+idEvento,
            async: false,
            type : 'GET'
            }, done, fail, always);
        };
        
        this.joinInEvent = function(idEvento,idUsuario,done, fail, always) {
            requestByAjax({
            url :  resourcePath+"?type=assistToEvent",
            data: {eventID:idEvento,userID:idUsuario},
            async: false,
            type : 'POST'
            }, done, fail, always);
        };
        
         this.addEvent = function(user_id,nombreEvento,descripcionEvento,ciudadEvento,fechaEvento,aforoEvento,etiquetaEvento,importanteEvento,imagenEvento,done,fail,always){
            requestByAjax({
            url :  resourcePath+"?type=add",
            data: {name:nombreEvento,description:descripcionEvento,location:ciudadEvento,day:fechaEvento,tag:etiquetaEvento,important:importanteEvento,capacity:aforoEvento,image:imagenEvento,organizer_id: user_id},
            async: false,
            type : 'POST'
            }, done, fail, always);
         };
        
        this.deleteEvent = function(userid, eventid, done, fail, always){
            requestByAjax({
            url : resourcePath+"?type=delete&eventID="+eventid+"&userID="+userid,
            async: false,
            type: 'DELETE'
            }, done, fail, always);
        };

        this.deleteAssistance = function(userid, eventid, done, fail, always){
            requestByAjax({
            url : resourcePath+"?type=unassistToEvent&eventID="+eventid+"&userID="+userid,
            async: false,
            type: 'DELETE'
            }, done, fail, always);
        };
        
    }
    return EventDAO;
})();
