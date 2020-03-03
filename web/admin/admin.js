/**
                                        `.------:::--...``.`                                        
                                    `-:+hmmoo+++dNNmo-.``/dh+...                                    
                                   .+/+mNmyo++/+hmmdo-.``.odmo -/`                                  
                                 `-//+ooooo++///////:---..``.````-``                                
                           `````.----:::/::::::::::::--------.....--..`````                         
           ```````````...............---:::-----::::---..------------------........```````          
        `:/+ooooooosssssssyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyysssssssssssssssssssssssssssoo+/:`       
          ``..-:/++ossyhhddddddddmmmmmarea51mbobmlazarmmmmmmmddddddddddddddhhyysoo+//:-..``         
                      ```..--:/+oyhddddmmmmmmmmmmmmmmmmmmmmmmmddddys+/::-..````                     
                                 ``.:oshddmmmmmNNNNNNNNNNNmmmhs+:.`                                 
                                       `.-/+oossssyysssoo+/-.`                                      
                                                                                                     
    
*/

$.widget("admin.loginPanel", {
    _create: function() {
        let self = this;

        self.element.html(loginForm);

        let $userLoginForm = self.element.find('[data-role=author_login]');
        let $userPasswordForm = self.element.find('[data-role=author_password');
        let $loginButton = self.element.find('[data-role=perform_login');


        $loginButton.click(function () {
            var loginInfo = {
                login: $userLoginForm.val(),
                passwordHash: $userPasswordForm.val()
            };

            let authorize = $.ajax({
                url: "/admin/jsonApi.jsp",
                method: "POST",
                data: { data: JSON.stringify(loginInfo), action: "authorize"}
            });

            authorize.done(function(adminResponseJson){
                let adminResponse = JSON.parse(adminResponseJson);
                if (!adminResponse.success){
                    alert("ACCESS DENIED")
                } else {
                    let authorizedVO = adminResponse.data;
                    Cookies.set('guid', authorizedVO.guid);
                    Cookies.set('privilegeLevelName', authorizedVO.privilegeLevelName);
                    Cookies.set('displayName', authorizedVO.displayName);
                    Cookies.set('authorId', authorizedVO.authorId);
                    self.element.editorPanel();
                }
            });
        });

    }

});

$.widget("admin.editorPanel", {
    _create: function () {
        let self = this;

        let login = Cookies.get('displayName');
        let hAdministrationRoot = Handlebars.compile(administrationRoot);

        self.element.html(hAdministrationRoot({login:login}));

        let $board = self.element.find("[data-role=admin-board]");
        let $authorsEditButton = self.element.find('[data-role=edit-authors]');
        let $photosEditButton = self.element.find('[data-role=edit-photos');
        let $galleriesEditButton = self.element.find('[data-role=edit-galleries]');
        let $articlesEditButton = self.element.find('[data-role=edit-articles]');
        let $logout = self.element.find('[data-role=logout]');

        $authorsEditButton.unbind();
        $authorsEditButton.click(function(){
            $board.html("");
            $board.authorsEdit();
        });

        $photosEditButton.unbind();
        $photosEditButton.click(function(){
            $board.html("");
            $board.photosWidget();
        });

        $galleriesEditButton.unbind();
        $galleriesEditButton.click(function(){
            $board.html("");
            $board.galleriesWidget();
        });

        $articlesEditButton.unbind();
        $articlesEditButton.click(function(){
            $board.html("");
            $board.articlesWidget();
        });

        $logout.unbind();
        $logout.click(function(){
            Cookies.remove('guid');
            Cookies.remove('privilegeLevelName');
            Cookies.remove('displayName');
            Cookies.remove('authorId');

            location.reload();
        });



    }
});
