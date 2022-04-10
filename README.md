<div align="center">
    <h1> XAutoDaily </h1>

[![license](https://img.shields.io/github/license/teble/XAutoDaily.svg)](https://www.gnu.org/licenses/gpl-3.0.html)
[![GitHub release](https://img.shields.io/github/release/teble/XAutoDaily.svg)](https://github.com/teble/XAutoDaily/latest)
[![Telegram](https://img.shields.io/static/v1?label=Telegram&message=Channel&color=0088cc)](https://t.me/XAutoDaily)
[![Telegram](https://img.shields.io/static/v1?label=Telegram&message=Chat&color=0088cc)](https://t.me/XAutoDailyChat)
</div>

XAutoDaily 是一个兼容QQ大部分版本(包括新版TIM)的开源签到 Xposed 模块

-----

## 使用方法

激活本模块后，在 QQ 客户端的设置中点击 "XAutoDaily 设置" 即可开关对应功能。

- Android >= 7.0
- QQ >= 8.0.0, TIM(新版全功能支持，旧版**可能**不兼容小程序签到)

## 对于部分用户的特殊说明

- **安卓12太极阴64位QQ用户**: 由于太极在安卓12上可能存在bug，勾选XA后会导致QQ闪退（根据日志查看，art崩溃不属于模块本身的bug），请更换32位QQ或者更换框架以解决问题。
- **vivo(包括子产品)全面屏用户**: **可能**存在由于未知魔改导致模块Compose UI渲染白屏的问题，只能通过**切屏**(前台后台切换)/**切换虚拟按键**(虚拟导航)来解决问题。
- **转生用户**: 由于框架本身可能存在bug，**部分用户**在勾选模块后可能无法生效（加载模块流程中断导致后续模块不加载），请先勾选xa再勾选其它模块(根据勾选顺序先后加载模块)。

## 功能介绍

- 会员任务以及签到
- 黄钻签到
- 腾讯视频会员打卡
- 会员排行榜点赞
- QQ日签卡
- 小程序打卡/任务
- 大会员任务(个性赞需要手动点赞)
- 好友名片自动点赞回赞
- 好友续火（避免滥用限制字符为20）
- 会员公众号签到
- 新版群打卡
- QQ好友字符抽取
- 萌宠任务
- 好友空间互访
- 好友名片点赞
- 好友续火

## 一切开发旨在学习，请勿用于非法用途

- 本项目保证永久开源，欢迎提交 Issue 或者 Pull Request，但是请不要提交用于非法用途的功能。
- 如果某功能被大量运用于非法用途，那么该功能将会被移除。
- 开发人员可能在任何时间**停止更新**或**删除项目**

## License

- [GPL-3.0](https://www.gnu.org/licenses/gpl-3.0.html)

```
Copyright (C) 2022 teble@github.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
