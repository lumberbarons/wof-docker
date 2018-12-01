$(document).ready(function() {
    $('#bundles').dataTable({
        ajax: function(data, callback, settings) {
            var url = '/inventory.json';
            $.getJSON(url, function(bundles) {
                let rows = [];
                
                let latest = bundles.filter(function(bundle) { 
                    return bundle.name_compressed.endsWith('-latest.tar.bz2');
                });

                let global = latest.filter(function(bundle) {
                    return !bundle.name.startsWith('whosonfirst-data-constituency-') &&
                        !bundle.name.startsWith('whosonfirst-data-postalcode-') &&
                        !bundle.name.startsWith('whosonfirst-data-venue-');
                });
                
                global.forEach(function(bundle) {
                    rows.push([bundle.name, bundle.count, bundle.size_compressed, 
                        bundle.size, bundle.last_updated, bundle.last_modified]);
                });

                callback({data: rows});
            });
        },
        processing: true,
        columns: [
            { title: "Name" },
            { title: "Count" },
            { title: "Compressed Size" },
            { title: "Size" },
            { title: "Last Modified" },
            { title: "Last Updated" }
        ],
        columnDefs: [
            {
                render: function (data, type, row) {
                    if (type == "sort" || type == 'type') {
                        return data;
                    }
                    return filesize(data)
                },
                targets: [2,3]
            }
        ]
    });
});