var UsersDAO = (function() {

    function UsersDAO() {
    
   this.doLogin= function(login, password) {
      $.ajax({
       url: 'rest/users?login='+ login+"&password="+btoa(password),
           async:false,
       type: 'GET'
       })
       .done(function(user) {
       localStorage.setItem('authorization-token', btoa(login + ":" + password));
           localStorage.setItem('user',login);
           localStorage.setItem('user_id',user.id);
           document.getElementById('closeModal').click();
       })
       .fail(function() {
       alert('Invalid login and/or password.');
       });

   };
   
   
       this.doLogout=function(){
           localStorage.removeItem('authorization-token');
           localStorage.removeItem('user');
       };
    }
    return UsersDAO;
   })();