## 目录

```bash
# 前端
所有文件均在/frontend下

# 后端
所有文件均在/backend下

# Github仓库
.gitignore控制的是 整个 Git 仓库的忽略规则，不部署子模块的话必须放在根目录下

# README.md
即本文件 作为项目说明会在Github的repositories页显示
```

## 首次配置

```bash
# 克隆
接受邀请后启动cmd
在你想要储存项目的目录下输入：

git clone https://github.com/Crush6x9/SimpleErrandSystem.git

这条命令会在当前目录下自动新建一个叫 SimpleErrandSystem 的文件夹，并把整个项目拉下来

```

## 稳定的克隆/拉取方法

```bash
打开命令提示符CMD运行：

bashssh-keygen -t ed25519 -C "你的邮箱@example.com"

按提示操作：

textGenerating public/private ed25519 key pair.
Enter file in which to save the key (C:\Users\你的用户名\.ssh\id_ed25519):

直接回车 → 默认保存到 C:\Users\你的用户名\.ssh\id_ed25519

textEnter passphrase (empty for no passphrase):
Enter same passphrase again:

建议直接回车两次（不设密码），方便使用；如果设了密码每次 git 都要输，麻烦

textYour identification has been saved in ...
Your public key has been saved in ...id_ed25519.pub
The key fingerprint is:
SHA256:8k4y6WPPMqI5IRyp8cy3h7z2IlEcEBoCkGHIQPwf40w 你的邮箱@example.com

看到这个指纹，说明生成成功

打开公钥文件并复制内容：

bashnotepad %USERPROFILE%\.ssh\id_ed25519.pub


打开后全选（Ctrl+A）→ 复制（Ctrl+C）
内容长这样：
textssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAI... 你的邮箱@example.com

打开浏览器 → 登录 GitHub

右上角头像 → Settings → 左侧 SSH and GPG keys

点击绿色的 New SSH key
Title: 随便写，比如 My Windows PC
Key type: 默认 Authentication Key
Key: 粘贴你复制的内容
点击 Add SSH key

最后验证是否成功
还是进入CMD输入：

ssh -T git@github.com

成功提示：

textHi 用户名! You've successfully authenticated, but GitHub does not provide shell access.

第一次可能提示 Are you sure you want to continue connecting? → 输入 yes 回车

最后就可以输入以下命令拉取仓库了：

git clone git@github.com:Crush6x9/SimpleErrandSystem.git

```

## 更新！这一步非常重要，避免冲突！

```bash
# 最重要的一点：所有 git 命令必须在 SimpleErrandSystem 项目根目录下执行（就是 frontend 和 backend 的父目录）

# 每次开始写代码之前
在cmd的项目根目录下输入：

git pull origin main

拉取最新代码，确保你是基于最新版本修改的

# 每次更新完代码之后
启动cmd
在项目根目录下依次运行以下三条命令：

git add .
git commit -m "你的提交说明"
git push origin main

第一条命令add .只会上传所有你修改过的文件，所以不必担心缓存问题
```

## 我们的项目想要做些什么？

### N – Need/需求

在现代都市快节奏生活中，每个人都被琐事缠身：有一份亟待送达的文件却无法脱身，有一顿需要采购食材的晚餐却无心……“没空”与“急需”的冲突，正制造着无数个体的“时间困境”；“有时间”却“没精力”的矛盾，也使得一些人不愿在一些事情上花费心思。与此同时，也有一群充满活力的人，他们希望利用碎片时间，为自己与他人创造价值。市场渴求一个能极速解决问题，也能让邻里相熟、让人与人温暖连接的解决方案。
由此，我们决定打造“跑了个腿”跑腿系统。


### A – Approach/方法

用户在系统上提交需求、标注报酬与时限，系统将推送给各位跑腿员，瞬间响应、极速接单，确保每个委托都像按下了快进键。完成任务后，用户和跑腿员可以互相打分评价，以赞赏温暖人心。


### B – Benefit/好处

委托人能够从忙碌中解放，拥抱轻松与效率。无论是“救命”的急事，还是“懒得动”的琐事，都有人积极响应，飞奔而至。
接单员将在自由安排的时间里，获得经济回报与助人之乐。每一次顺利完成委托，都是一次价值的实现，积累的是信誉与社区的尊重。


### C – Competition/竞争

我们强调“邻里相助”与“价值认同”，这远胜于纯商业化的雇佣关系，能构建更稳固、更温暖的信任壁垒，建立深厚的社区互助文化。


### D – Delivery/推广

我们将在校园中开始我们的旅程，在校园招募学生成为委托人或跑腿员，通过校内高频的代办需求，一点点推广系统。
