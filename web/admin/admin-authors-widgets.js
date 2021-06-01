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


$.widget("admin.authorsEdit", {
    _create: async function() {
        let self = this;
        await this._display(self);
    },

    _init: async function() {
        await this._display(this);
    },

    _createNewAuthor: async function(self, $button, buttonText) {
        let $newAuthorDisplayName = self.element.find('[data-role=new-author-display-name]');
        let $newAuthorLogin = self.element.find('[data-role=new-author-login]');
        let $newAuthorPassword = self.element.find('[data-role=new-author-password]');
        let $newAuthorPrivilegeLevelId = self.element.find('[data-role=new-author-privilege-level-id]:checked');

        let newAuthorDisplayName = $newAuthorDisplayName.val();
        let newAuthorLogin = $newAuthorLogin.val();
        let newAuthorPassword = $newAuthorPassword.val();
        let newAuthorPrivilegeId = $newAuthorPrivilegeLevelId.data('id');

        if (!newAuthorDisplayName){
            alert('empty author display name!');
            unSpinButton($button, buttonText);
            return;
        }

        if (!newAuthorLogin){
            alert('empty author login!');
            unSpinButton($button, buttonText);
            return;
        }

        if (!newAuthorPassword){
            alert('empty author password!');
            unSpinButton($button, buttonText);
            return;
        }

        if (newAuthorPrivilegeId === undefined || newAuthorPrivilegeId === null || newAuthorPrivilegeId === ""){
            alert('select privilege level!');
            unSpinButton($button, buttonText);
            return;
        }

        let newAuthor = {
            displayName: newAuthorDisplayName,
            login: newAuthorLogin,
            passwd: newAuthorPassword,
            privilegeLevel: newAuthorPrivilegeId
        };

        let res = await ajax({guid: Cookies.get('guid'), data: JSON.stringify(newAuthor), action: "createNewAuthor"}, "error creating user");
        if (res === undefined){
            unSpinButton($button, buttonText);
            return;
        }

        self._display(self);
    },

    _display: async function(self) {

        let guid = Cookies.get("guid");
        let currentUserPrivilegeLevelName = Cookies.get("privilegeLevelName");

        let listAllAuthorsVOResponse;
        let listPrivilegesResponse;


        $.when(
            $.ajax({
                url: 'jsonApi.jsp',
                method: 'POST',
                data: {guid: Cookies.get("guid"), action: "listAllAuthorsVO"},
                success: function(adminResponseJson){
                    listAllAuthorsVOResponse = JSON.parse(adminResponseJson);
                }
            }),
            $.ajax({
                url: 'jsonApi.jsp',
                method: 'POST',
                data: {action: "listPrivileges"},
                success: function(adminResponseJson){
                    listPrivilegesResponse = JSON.parse(adminResponseJson);
                }
            })
        ).then(function(){
            if (!listAllAuthorsVOResponse.success){
                alert('Not authorized! '+listAllAuthorsVOResponse.errorDescription);
            } else {
                let authorVOs = listAllAuthorsVOResponse.data;
                let privilegeVOs = listPrivilegesResponse.data;

                self.element.html(authorsEditRoot);

                let userIsSuperuser = currentUserPrivilegeLevelName === "superuser";
                let userIsDemo = currentUserPrivilegeLevelName === "demo";


                let hRegisteredAuthorsPanel = Handlebars.compile(registeredAuthorsPanel);
                $('[data-role=registeredAuthorsPanel]').html(hRegisteredAuthorsPanel({authorVOs: authorVOs}));
                $('[data-toggle="tooltip"]').tooltip();

                let hAddNewAuthorPanel = Handlebars.compile(addNewAuthorPanel);
                $('[data-role=addNewAuthorPanel]').html(hAddNewAuthorPanel({privilegeVOs: privilegeVOs}));

                /**
                 * enable or disable elements according to user privileges
                 */

                $('button[data-role=delete-user]').prop("disabled", false);

                if (userIsSuperuser){
                    $('button[data-role=change-display-name]').prop("disabled", false);
                    $('button[data-role=change-password]').prop("disabled", false);
                    $('button[data-role=change-privilege]').prop("disabled", false);


                    $('[data-user=super]').prop("disabled", false);
                } else {
                    let authorId = Cookies.get('authorId');

                    if (!isDemo()) {
                        $('button[data-role=change-display-name][data-id=' + authorId + ']').prop("disabled", false);
                        $('button[data-role=change-password][data-id=' + authorId + ']').prop("disabled", false);
                    }
                }

                /**
                 * create new author - process button click
                 */

                let $createNewAuthorButton = $('[data-role=new-author-create]');

                $createNewAuthorButton.unbind();
                $createNewAuthorButton.click(function(){
                    let buttonText = spinButton($createNewAuthorButton);
                    self._createNewAuthor(self, $createNewAuthorButton, buttonText);
                });


                /**
                 * change password modal and api call
                 */
                $('button[data-role=change-password]').unbind();
                $('button[data-role=change-password]').click(function(){
                    let targetUserId = $(this).data("id");
                    $('[data-role=change-password-modal]').modal('show');

                    $('button[data-role=update-user-password]').unbind();
                    $('button[data-role=update-user-password]').click(function(){

                        let newPassword = $('[data-role=new-user-password]').val();
                        $('[data-role=new-user-password]').val('');

                        let changeAuthorDataRequest = {
                            targetAuthorId: targetUserId,
                            newDisplayName: null,
                            newPassword: newPassword,
                            newAccessLevelId: null
                        };

                        let changePassword = $.ajax({
                            url: 'jsonApi.jsp',
                            method: 'POST',
                            data: {guid: Cookies.get("guid"), data: JSON.stringify(changeAuthorDataRequest), action: "changePassword"}
                        });

                        changePassword.done(function(adminResponseJson){
                            let adminResponse = JSON.parse(adminResponseJson);

                            if (!adminResponse.success){
                                alert("can't update password: "+adminResponse.errorDescription);
                            } else {
                                $('[data-role=change-password-modal]').modal('hide');
                                $('[data-role="update-user-password"]').val('');
                                self._display(self);
                            }
                        });
                    });
                });


                /**
                 * change display name modal and api call
                 */
                $('button[data-role=change-display-name]').unbind();
                $('button[data-role=change-display-name]').click(function(){
                    let targetUserId = $(this).data("id");
                    $('[data-role=change-displayname-modal]').modal('show');

                    $('button[data-role=update-user-displayname]').unbind();
                    $('button[data-role=update-user-displayname]').click(function(){

                        let newDisplayName = $('[data-role=new-user-displayname]').val();
                        $('[data-role=new-user-displayname]').val('');

                        let changeAuthorDataRequest = {
                            targetAuthorId: targetUserId,
                            newDisplayName: newDisplayName,
                            newPassword: null,
                            newAccessLevelId: null
                        };

                        let changeDisplayName = $.ajax({
                            url: 'jsonApi.jsp',
                            method: 'POST',
                            data: {guid: Cookies.get("guid"), data: JSON.stringify(changeAuthorDataRequest), action: "changeDisplayName"}
                        });

                        changeDisplayName.done(function(adminResponseJson){
                            let adminResponse = JSON.parse(adminResponseJson);

                            if (!adminResponse.success){
                                alert("can't update displayname: "+adminResponse.errorDescription);
                            } else {
                                $('[data-role=change-displayname-modal]').modal('hide');
                                $('[data-role="update-user-displayname"]').val('');
                                self._display(self);
                            }
                        });
                    });
                });

                /**
                 * change privilege level modal and api call
                 */
                $('button[data-role=change-privilege]').unbind();
                $('button[data-role=change-privilege]').click(function(){
                    let targetUserId = $(this).data("id");
                    $('[data-role=change-access-level-modal]').modal('show');

                    $('button[data-role=update-user-access-level]').unbind();
                    $('button[data-role=update-user-access-level]').click(function(){

                        let newPrivilegeLevelId = $('[data-role=update-author-privilege-level-id]:checked').data("id");

                        let changeAuthorDataRequest = {
                            targetAuthorId: targetUserId,
                            newDisplayName: null,
                            newPassword: null,
                            newAccessLevelId: newPrivilegeLevelId
                        };

                        let changeAccessLevel = $.ajax({
                            url: 'jsonApi.jsp',
                            method: 'POST',
                            data: {guid: Cookies.get("guid"), data: JSON.stringify(changeAuthorDataRequest), action: "changeAccessLevel"}
                        });

                        changeAccessLevel.done(function(adminResponseJson){
                            let adminResponse = JSON.parse(adminResponseJson);

                            if (!adminResponse.success){
                                alert("can't update privilege level: "+adminResponse.errorDescription);
                            } else {
                                $('[data-role=change-access-level-modal]').modal('hide');
                                self._display(self);
                            }
                        });
                    });
                });

                /*
                delete user
                 */

                let $deleteButton = self.element.find('[data-role=delete-user]');

                if (isDemo()){
                    $deleteButton.prop("disabled", true);
                }

                $deleteButton.click(function(){
                    let targetUserId = $(this).data("id");
                    $('[data-role="delete-user-confirm"]').modal('show');

                    $('[data-role="delete-confirm"]').unbind();
                    $('[data-role="delete-confirm"]').click(function(){
                        let deleteUser = $.ajax({
                            url: 'jsonApi.jsp',
                            method: 'POST',
                            data: {guid: Cookies.get("guid"), data: targetUserId, action: "deleteUser"}
                        });

                        deleteUser.done(function(adminResponseJson){
                            let adminResponse = JSON.parse(adminResponseJson);

                            if (!adminResponse.success){
                                alert("can't delete user: "+adminResponse.errorDescription);
                            } else {
                                $('[data-role="delete-user-confirm"]').modal('hide');
                                self._display(self);
                            }
                        });
                    });

                });


            }
        });
    }
});
