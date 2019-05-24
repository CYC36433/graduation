<template>
  <div class="login-container">
    <el-form autoComplete="on" :model="loginForm" :rules="loginRules" ref="loginForm" label-position="left"
             label-width="0px" v-if="handleFlag==1" class="card-box login-form">
      <h3 class="title">图书馆图书管理系统</h3>
      <el-form-item prop="username">
        <span class="svg-container svg-container_login">
          <svg-icon icon-class="user"/>
        </span>
        <el-input v-model="loginForm.username" autoComplete="on"/>
      </el-form-item>
      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password"></svg-icon>
        </span>
        <el-input type="password" @keyup.enter.native="handleLogin" v-model="loginForm.password"
                  autoComplete="on"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" style="width:100%;" :loading="loading" @click.native.prevent="handleLogin">
          登录
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" style="width:100%;" @click.native.prevent="handleRegister">
          注册
        </el-button>
      </el-form-item>
    </el-form>
    <el-form v-else autoComplete="on" :model="newUser" :rules="userRules" ref="newUser" label-position="right"
             label-width="100px"
             class="register-form">
      <h3 class="title">图书管理系统</h3>
      <el-form-item prop="username" label="用户名：">
        <el-input v-model="newUser.username" autoComplete="on"/>
      </el-form-item>
      <el-form-item prop="password" label="密码：">
        <el-input type="password" v-model="newUser.password"
                  autoComplete="on"></el-input>
      </el-form-item>
      <el-form-item prop="checkPass" label="确认密码：">
        <el-input type="password" v-model="newUser.checkPass"
                  autoComplete="on"></el-input>
      </el-form-item>
      <el-form-item>
      <el-button type="success" style="width:130%;margin-left:-30%" @click="register">
        立即注册
      </el-button>
      </el-form-item>
      <el-form-item>
      <el-button type="primary" style="width:130%;margin-left:-30%" @click="returnLogin">
        返回登录
      </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
  export default {
    name: 'login',
    data() {
      return {
        loginForm: {
          username: null,
          password: null
        },
        loginRules: {
          username: [{required: true, trigger: 'blur', message: "请输入用户名"}],
          password: [{required: true, trigger: 'blur', message: "请输入密码"}]
        },
        newUser: {
          username: null,
          password: null,
          checkPass: null,
          nickname: '会员'
        },
        userRules: {
          username: [{required: true,trigger: 'blur', message: "请输入用户名"}],
          password: [{required: true,trigger: 'blur', message: "请输入密码"}],
          checkPass: [{required: true,trigger: 'blur', message: "请再次输入密码"}]
        },
        loading: false,
        handleFlag: 1
      }
    },
    methods: {
      handleLogin() {
        this.$refs.loginForm.validate(valid => {
          if (valid) {
            this.loading = true
            this.$store.dispatch('Login', this.loginForm).then(data => {
              this.loading = false
              if ("success" === data.result) {
                this.$router.push({path: '/'});
              } else {
                this.$message.error("账号/密码错误");
              }
            }).catch(() => {
              this.loading = false
            })
          } else {
            return false
          }
        })
      },
      handleRegister(){
        this.handleFlag = 2;
        this.newUser = {username: null,password: null,checkPass: null,nickname: '会员'};
      },
      returnLogin(){
        this.handleFlag = 1;
        this.newUser = {username: null,password: null,checkPass: null,nickname: '会员'};
      },
      register(){
        this.$refs.newUser.validate(valid => {
          if(valid) {
          if(this.newUser.password === this.newUser.checkPass){
            this.api({
              url: '/login/register',
              method: 'post',
              data: this.newUser
            }).then(()=>{
              this.$message.success('注册成功，首次登录请完善用户信息！');
              this.handleFlag = 1;
            }).catch(()=>{
              this.$message.error('注册失败，用户名已存在');
            })
            this.newUser = {username: null,password: null,checkPass: null,nickname: '会员'};
          }else{
            this.$message.error('两次密码输入不一致');
          }
          }
          else{
            return false
          }
          }
        )
      }
    }
  }
</script>
<style rel="stylesheet/scss" lang="scss">
  @import "../../styles/mixin.scss";
  $bg: #2d3a4b;
  $dark_gray: #889aa4;
  $light_gray: #eee;

  .login-container {
    @include relative;
    height: 100vh;
    background-color: $bg;
    input:-webkit-autofill {
      -webkit-box-shadow: 0 0 0px 1000px #293444 inset !important;
      -webkit-text-fill-color: #fff !important;
    }
    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
    }
    .el-input {
      display: inline-block;
      height: 47px;
      width: 85%;
    }
    .tips {
      font-size: 14px;
      color: #fff;
      margin-bottom: 10px;
    }
    .svg-container {
      padding: 6px 5px 6px 15px;
      color: $dark_gray;
      vertical-align: middle;
      width: 30px;
      display: inline-block;
      &_login {
        font-size: 20px;
      }
    }
    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
    .login-form {
      position: absolute;
      left: 0;
      right: 0;
      width: 400px;
      padding: 35px 35px 15px 35px;
      margin: 120px auto;
    }
    .login-form .el-form-item {
      border: 1px solid rgba(255, 255, 255, 0.1);
      background: rgba(0, 0, 0, 0.1);
      border-radius: 5px;
      color: #454545;
    }
    .register-form{
      position: absolute;
      left: 0;
      right: 0;
      width: 400px;
      padding: 35px 35px 15px 35px;
      margin: 120px auto;
    }
    .register-form .el-form-item .el-form-item__label{
      color: #eee;
    }
    .register-form .el-form-item .el-form-item__content{
      border: 1px solid rgba(255, 255, 255, 0.1);
      background: rgba(0, 0, 0, 0.1);
      border-radius: 5px;
      color: #454545;
    }
    .show-pwd {
      position: absolute;
      right: 10px;
      top: 7px;
      font-size: 16px;
      color: $dark_gray;
      cursor: pointer;
    }
    .thirdparty-button {
      position: absolute;
      right: 35px;
      bottom: 28px;
    }
  }
</style>
