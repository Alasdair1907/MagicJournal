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

$.widget('magic.pager', {

    options: {
        filter: {needArticles: true, needPhotos: true, needGalleries: true, page: 0},
        pagesTotal: 1
    },

    _init: function(){
        let self = this;
        let ops = this.options;

        let pages = [];

        // 1 based numeration
        var curMinus = ops.filter.page+1-1;
        var curPlus = ops.filter.page+1+1;

        pages.push({
            paramsStr: getParamsStrFromPagingRequestFilter("posts.jsp", ops.filter),
            pageNum: ops.filter.page+1,
            isActive: true
        });

        let i = 10;
        if (ops.pagesTotal < i){
            i = ops.pagesTotal;
        }

        for (let t = 1; t < i; t++){

            if (  (i % 2 === 0 && curMinus > 0) ||  (i % 2 !== 0 && curPlus > ops.pagesTotal)  ){

                ops.filter.page = curMinus-1;

                pages.unshift({
                    paramsStr: getParamsStrFromPagingRequestFilter("posts.jsp", ops.filter),
                    pageNum: curMinus,
                    isActive: false
                });
                curMinus -= 1;
            } else {

                ops.filter.page = curPlus-1;

                pages.push({
                    paramsStr: getParamsStrFromPagingRequestFilter("posts.jsp", ops.filter),
                    pageNum: curPlus,
                    isActive: false
                });
                curPlus += 1;
            }
        }

        let hPagerTemplate = Handlebars.compile(pagerTemplate);
        self.element.html(hPagerTemplate({pages: pages}));

    }
});