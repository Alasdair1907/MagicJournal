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

$.widget("magic.DynamicSearchCda", {

    _toggleSelect: function($selectable){
        if ($selectable.data("selected") !== "selected"){
            this._setSelected($selectable);
        } else {
            this._setNotSelected($selectable);
        }
    },

    _setSelected: function($selectable){
        $selectable.removeClass("selectable-default");

        let customClass = $selectable.data("class");
        if (customClass){
            $selectable.addClass(customClass)
        } else {
            $selectable.addClass("selectable-selected");
        }

        $selectable.attr("data-selected", "selected");
    },

    _setNotSelected: function($selectable){

        let customClass = $selectable.data("class");
        if (customClass){
            $selectable.removeClass(customClass);
        } else {
            $selectable.removeClass("selectable-selected");
        }

        $selectable.addClass("selectable-default");
        $selectable.attr("data-selected", "none");
    },

    _create: async function(){
        let self = this;
        let ops = this.options;

        self.pagingRequestFilter = ops.pagingRequestFilter;
        self.callback = ops.callback;
        self.geo = ops.geo;
        self.clickOnCreate = ops.clickOnCreate;

        self._display();
    },
    
    _display: async function(){
        let self = this;

        let pagingRequestFilter = self.pagingRequestFilter;

        if (self.geo){
            pagingRequestFilter.requireGeo = true;
        }

        let preFilteredTags = await ajaxCda({action: "preFilterTags", data: JSON.stringify(pagingRequestFilter)});

        let hDynamicSearchCdaTemplate = Handlebars.compile(dynamicSearchCdaTemplate);
        self.element.html(hDynamicSearchCdaTemplate({tags: preFilteredTags}));

        let $articlesSelectable = self.element.find('[data-role="post-type"][data-id="articles"]');
        let $photosSelectable = self.element.find('[data-role="post-type"][data-id="photos"]');
        let $galleriesSelectable = self.element.find('[data-role="post-type"][data-id="galleries"]');

        if (pagingRequestFilter.needArticles){
            self._setSelected($articlesSelectable);
        }

        if (pagingRequestFilter.needPhotos){
            self._setSelected($photosSelectable);
        }

        if (pagingRequestFilter.needGalleries){
            self._setSelected($galleriesSelectable);
        }

        if (pagingRequestFilter.tags){
            $.each(pagingRequestFilter.tags, function(index, tag){
                let $tagSelectable = self.element.find('[data-role="post-tag"][data-id="' + tag + '"]');
                self._setSelected($tagSelectable);
            });
        }

        let $typesSelectables = self.element.find('[data-role="post-type"]');
        let $tagsSelectables = self.element.find('[data-role="post-tag"]');

        $typesSelectables.unbind();
        $typesSelectables.click(function(){
            self._toggleSelect($(this));
            self.enrichFilter();
            self._display();
        });

        $tagsSelectables.unbind();
        $tagsSelectables.click(function(){
            self._toggleSelect($(this));
            self.enrichFilter();
            self._display();
        });

        let $searchButton = self.element.find('[data-role="search-posts"]');
        $searchButton.unbind();
        $searchButton.click(function(){
            self.callback(self.getPagingRequestFilter());
        });

        if (self.clickOnCreate && !self.creationDone){
            $searchButton.click();
            self.creationDone = true;
        }

    },
    enrichFilter: function(){
        let self = this;
        
        let tags = [];
        let nothingSelected = true;

        let pagingRequestFilter = self.pagingRequestFilter;

        pagingRequestFilter.needArticles = false;
        pagingRequestFilter.needPhotos = false;
        pagingRequestFilter.needGalleries = false;

        let $postTypes = self.element.find('[data-role="post-type"][data-selected="selected"]');
        $.each($postTypes, function(index, value){
            if ($(value).data("id") === "articles"){
                pagingRequestFilter.needArticles = true;
                nothingSelected = false;
            }
            if ($(value).data("id") === "photos"){
                pagingRequestFilter.needPhotos = true;
                nothingSelected = false;
            }
            if ($(value).data("id") === "galleries"){
                pagingRequestFilter.needGalleries = true;
                nothingSelected = false;
            }
        });

        if (nothingSelected){
            return null;
        }

        let $tags = self.element.find('[data-role="post-tag"][data-selected="selected"]');
        $.each($tags, function(index, value){
            tags.push($(value).data("id"));
        });

        pagingRequestFilter.tags = tags;

        if (self.geo){
            pagingRequestFilter.requireGeo = true;
        }

    },
    resetPagingRequestFilterPage: function(){
        this.pagingRequestFilter.page = 0;
    },
    getPagingRequestFilter: function(){
        this.resetPagingRequestFilterPage();
        this.enrichFilter();
        return this.pagingRequestFilter;
    }
});