modules = {
    'inviter' {
        resource url:  [dir: 'js', file: 'inviter.js', plugin: 'inviter']
        resource url:  [dir: 'css', file: 'inviter.css', plugin: 'inviter']
        // images for default providers
        resource url:  [dir: 'images', file: 'facebook.png', plugin: 'inviter']
        resource url:  [dir: 'images', file: 'google.png', plugin: 'inviter']
        resource url:  [dir: 'images', file: 'linkedin.png', plugin: 'inviter']
        resource url:  [dir: 'images', file: 'twitter.png', plugin: 'inviter']
        resource url:  [dir: 'images', file: 'windowslive.png', plugin: 'inviter']
        resource url:  [dir: 'images', file: 'yahoo.png', plugin: 'inviter']
        // image for contacts without their own pic
        resource url:  [dir: 'images', file: 'empty.png', plugin: 'inviter']
    }
}