GameReminder
============

GameReminder is an Android app for reminding you sport games broadcasted on TV.

It crawls sport game TV broadcasting info from web, and reminder you through google's calendar.

Nowadays, GameReminder support display game info from SINA.com NBA channel and azhibo.com NBA channel. It will add more data sources later.

[APK download](http://github.com/dalang/gamereminder/raw/master/GameReminder.apk) v2.1.1

##TODO
* pull down to refresh will lead to crash sometimes 


##ChangeLog
###v2.1.1
* Pager "已设提醒" 数据empty时，listview一直现实刷新图标
* 从Parger 1切换到Pager 2时，会弹出"哎呀，出错了"
* 添加、删除提醒后，Pager"已设提醒"自动更新
* Pager进行pulldown更新时，listview会只显示一个item

###v2.1
add game info data source from [azhibo.com](http://www.azhibo.com/nbazhibo)
##Notice

Since the reminding functionality is based on google calendar, **an available google calendar account will be needed** if you want to the reminding functionality work well.

##System Requirement
Android 2.3.3 version or greater

##Contact Me
[Weibo](http://weibo.com/iDalang)

[Gmail](mailto:donguoxing@gmail.com)

##Acknowledge:<br/>

The following libraries are included in this project.

* [htmlparser](http://htmlparser.sourceforge.net/)
* [ActionBarSherlock](http://actionbarsherlock.com/)
* [Pull-to-Refresh](https://github.com/chrisbanes/Android-PullToRefresh)
* [ViewPagerIndicator](http://viewpagerindicator.com/)
* [android-comboseekbar](https://github.com/karabaralex/android-comboseekbar)

##Screenshot
![first img](http://github.com/dalang/gamereminder/raw/master/screenshot/01.jpg)
