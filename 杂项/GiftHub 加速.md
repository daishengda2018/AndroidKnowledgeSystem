> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 [gist.github.com](https://gist.github.com/chuyik/02d0d37a49edc162546441092efae6a1)

分辨需要设置的代理
-----------------------

*   HTTP 形式：
  
    > git clone [https://github.com/owner/git.git](https://github.com/owner/git.git)
    
*   SSH 形式：
  
    > git clone [git@github.com](mailto:git@github.com):owner/git.git
    

一、HTTP 形式
----------------------

### 走 HTTP 代理

```sh
git config --global http.proxy "http://127.0.0.1:8080"
git config --global https.proxy "http://127.0.0.1:8080"
```

### 走 socks5 代理（如 Shadowsocks）

```sh
git config --global http.proxy "socks5://127.0.0.1:1080"
git config --global https.proxy "socks5://127.0.0.1:1080"
```

### 取消设置

```sh
git config --global --unset http.proxy
git config --global --unset https.proxy
```

二、SSH 形式
--------------------

修改 `~/.ssh/config` 文件（不存在则新建）：

```sh
# 必须是 github.com
Host github.com
   HostName github.com
   User git
   # 走 HTTP 代理
   # ProxyCommand socat - PROXY:127.0.0.1:%h:%p,proxyport=8080
   # 走 socks5 代理（如 Shadowsocks）
   # ProxyCommand nc -v -x 127.0.0.1:1080 %h %p
```