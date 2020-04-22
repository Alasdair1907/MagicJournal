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
    _create: async function() {
        let self = this;

        let settingsTO = await ajax({action: "getSettingsNoAuth"});
        let demoAllowed = false;

        if (settingsTO !== undefined && settingsTO.allowDemoAnon){
            demoAllowed = true;
        }

        self.element.html(loginForm);

        if (demoAllowed){
            let $demoAnonForm = self.element.find('[data-role="demo-log-in"]');
            $demoAnonForm.removeAttr('hidden');
            let $demoLogInButton = $demoAnonForm.find('[data-role="demo_perform_login"]');

            $demoLogInButton.click(await async function(){
                let bt = spinButton($demoLogInButton);

                let authorizedVO = await ajax({action: "authorizeDemo"}, "can not perform authorization");
                if (authorizedVO){
                    Cookies.set('guid', authorizedVO.guid);
                    Cookies.set('privilegeLevelName', authorizedVO.privilegeLevelName);
                    Cookies.set('displayName', authorizedVO.displayName);
                    Cookies.set('authorId', authorizedVO.authorId);
                    self.element.editorPanel();
                }

                unSpinButton($demoLogInButton, bt);
            });
        }

        let $userLoginForm = self.element.find('[data-role=author_login]');
        let $userPasswordForm = self.element.find('[data-role=author_password]');
        let $loginButton = self.element.find('[data-role=perform_login]');

        $userLoginForm.keyup(function(event){
            if (event.key == "Enter"){ $loginButton.click()}
        });

        $userPasswordForm.keyup(function(event){
            if (event.key == "Enter"){ $loginButton.click()}
        });

        $loginButton.click(function () {

            let bt = spinButton($loginButton);

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

                unSpinButton($loginButton, bt);
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
        let $settingsEditButton = self.element.find('[data-role="edit-settings"]');

        let $profileEdit = self.element.find('[data-role="edit-profile"]');
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

        $profileEdit.unbind();
        $profileEdit.click(function(){
            $board.html("");
            $board.authorProfile();
        });

        $logout.unbind();
        $logout.click(function(){
            Cookies.remove('guid');
            Cookies.remove('privilegeLevelName');
            Cookies.remove('displayName');
            Cookies.remove('authorId');

            location.reload();
        });

        $settingsEditButton.unbind();
        $settingsEditButton.click(function(){
            $board.html("");
            $board.settingsEditor();
        });

    }
});
