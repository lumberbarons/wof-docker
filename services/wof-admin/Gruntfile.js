module.exports = function(grunt) {

    grunt.initConfig({
      pkg: grunt.file.readJSON('package.json'),
      cacheBust: {
        taskName: {
          options: {
            assets: [
                'app/**', 
                'bower_components/**',
                'content/**'
            ],
            baseDir: './build/resources/main/static',
            queryString: true
          },
          src: ['./build/resources/main/static/index.html']
        }
      }
    });
  
    grunt.loadNpmTasks('grunt-cache-bust');
    
    grunt.registerTask('default', ['cacheBust']);
  
  };