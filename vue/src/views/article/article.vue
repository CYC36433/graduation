<template>
  <div class="app-container">
    <div class="filter-container" style="margin-left:80px;padding: 0 10px;width:80%">
      <el-input placeholder="请输入书名或出版社名搜索" v-model="filter.search_content" style="width:250px;margin:15px 0" size="small">
        <el-button slot="append" icon="el-icon-search"></el-button>
      </el-input>
      <el-select placeholder="图书类型" v-model="filter.booktype" size="small" style="margin-left:30px">
        <el-option label="全部分类" value=""></el-option>
        <el-option label="历史类" value="历史类"></el-option>
        <el-option label="语言类" value="语言类"></el-option>
        <el-option label="计算机类" value="计算机类"></el-option>
        <el-option label="其他" value="其他"></el-option>
      </el-select>
      <el-button type="primary" v-if="hasPerm('article:add')" size="small" style="margin-left:20px" @click="handleBook(1)">添加图书</el-button>
    </div>
    <div class="books">
      <div class="item" v-for="book in tabledata">
        <img class="item_cover" :src="book.cover" @click="showInfo(book)"></img>
        <span class="item_info">{{book.book_name}}</span>
      </div>
    </div>
    <el-pagination
      @current-change="handleCurrentChange"
      :current-page.sync="pageNum"
      :total="count"
      :page-size="12"
      layout="total, prev, pager, next, jumper" class="page">
    </el-pagination>
    <el-dialog class="infoDialog" :title="current_book.book_name" :visible.sync="infoDialogVisible" width="40%">
      <el-row>
        <el-col :span="12" style="display:inline-block width:auto"><img style="width:80%;height:400px" :src="current_book.cover"></img></el-col>
        <el-col :span="12">
          <el-form :model="current_book" class="book_info">
            <el-form-item label="图书编号："><span>{{current_book.book_id}}</span></el-form-item>
            <el-form-item label="图书类别："><span>{{current_book.type_name}}</span></el-form-item>
            <el-form-item label="图书名称："><span>{{current_book.book_name}}</span></el-form-item>
            <el-form-item label="出版社："><span>{{current_book.book_press}}</span></el-form-item>
            <el-form-item label="出版时间："><span>{{current_book.publicationtime}}</span></el-form-item>
            <el-form-item label="内容简介："><span>{{current_book.book_summary}}</span></el-form-item>
            <el-form-item v-if="hasPerm('article:add')" label="数量："><span>{{current_book.book_qty}}</span></el-form-item>
          </el-form>
        </el-col>
      </el-row>    
      <el-row slot="footer" style="margin-top:-5px">
        <div v-if="hasPerm('article:add')">
          <el-button type="primary" @click="handleBook(2)">修改</el-button>
          <el-button type="success" @click="deleteBook">删除</el-button>
        </div>
        <div v-else>
          <el-button type="primary" @click="borrowBook">借阅</el-button>
          <el-button type="success" @click="infoDialogVisible = false">关闭</el-button>
        </div>
      </el-row>
    </el-dialog>
    <el-dialog class="borrowDialog" title="借阅信息" :visible.sync="borrowDialogVisible" width="20%" :modal="false">
      <el-form style="margin-left:10px" label-width="100px" label-position="right">
        <el-form-item label="借阅人："><span>{{$store.state.user.nickname}}</span></el-form-item>
        <el-form-item label="借阅图书："><span>{{current_book.book_name}}</span></el-form-item>
        <el-form-item label="借阅期：">
          <el-select v-model="borrowtime" placeholder="请选择借阅时间">
            <el-option label="一周" value="7"></el-option>
            <el-option label="一个月" value="30"></el-option>
            <el-option label="两个月" value="60"></el-option>
            <el-option label="三个月" value="90"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="borrow" :disabled="borrowtime==''">确定借阅</el-button>
        <el-button type="success" @click="borrowDialogVisible = false">取消</el-button>
      </div>
    </el-dialog>
    <el-dialog class="handleDialog" title="图书信息" :visible.sync="handleDialogVisible" width="30%">
      <el-form :model="current_book" class="handleForm" label-position="right" label-width="120px">
        <el-form-item label="图书编号：" prop="book_id">
          <span v-if="handleFlag == 2">{{current_book.book_id}}</span>
          <el-input v-else v-model="current_book.book_id" size="small"></el-input>
        </el-form-item>
        <el-form-item label="图书类别：" prop="type_name">
          <el-select placeholder="请选择图书类别" v-model="current_book.type_name" size="small" style="width:100%">
            <el-option label="历史类" value="历史类"></el-option>
            <el-option label="语言类" value="语言类"></el-option>
            <el-option label="计算机类" value="计算机类"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图书名称：" prop="book_name"><el-input v-model="current_book.book_name" size="small"></el-input></el-form-item>
        <el-form-item label="出版社：" prop="book_press"><el-input v-model="current_book.book_press" size="small"></el-input></el-form-item>
        <el-form-item label="出版时间：" prop="publicationtime">
          <el-date-picker v-model="current_book.publicationtime" type="date" placeholder="选择日期" format="yyyy 年 MM 月 dd 日" value-format="yyyy-MM-dd" size="small" style="width:100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="图书封面：" prop="book_cover">
          <el-input v-model="current_book.book_cover" size="small" v-if="false"></el-input>
          <el-upload
            class="cover-upload"
            ref="upload"
            :show-file-list="false"
            action="/api/book/upload"
            :on-change="handleChange"
            :auto-upload="false"
            :data="current_book"
          >
          <img v-if="uploadFlag" :src="current_book.cover" class="cover-avatar">
            <!-- :before-upload="beforeUpload" -->
          <i v-else class="el-icon-plus cover-upload-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="内容简介：" prop="book_summary"><el-input type="textarea" v-model="current_book.book_summary" size="small"></el-input></el-form-item>
        <el-form-item label="数量：" prop="book_qty"><el-input v-model="current_book.book_qty" size="small"></el-input></el-form-item>
        <el-form-item>
          <el-button type="success" v-if="handleFlag == 1" @click="addBook">保存</el-button>
          <el-button type="success" v-else @click="updateBook">保存</el-button>
          <el-button type="danger" @click="cancelHandle">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
  import format from "@/utils/index";
  export default {
    data() {
      return {
        book_list: [],
        listdata: [],
        current_book: {},
        listLoading: false,//数据加载等待动画
        infoDialogVisible: false,
        borrowDialogVisible: false,
        handleDialogVisible: false,
        handleFlag: 2,
        borrowtime: '',
        pageNum: 1,
        filter:{
          search_content:'',
          booktype:''
        },
        uploadFlag: false
      }
    },
    created() {
      this.getList();
    },
    computed: {
      tabledata(){
        var vm = this;
        vm.listdata = vm.book_list.filter(o=>{
          var b1 = vm.filter.search_content === "" || o.book_name.indexOf(vm.filter.search_content) > -1 || o.book_press.indexOf(vm.filter.search_content) > -1;
          var b2 = vm.filter.booktype === "" || o.type_name == vm.filter.booktype;
          return b1 && b2;
        })
        var result = vm.listdata.slice((vm.pageNum-1)*12,vm.pageNum*12);
        return result;
      },
      count(){
        var vm = this;
        return vm.listdata.length;
      }
    },
    methods: {
      getList() {
        //查询列表
        if (!this.hasPerm('article:list')) {
          return
        }
        this.listLoading = true;
        var a = this.api({
          url:"/book/listBook",
          method:"get",
        }).then(data=>{
          this.book_list = data.list;
          this.listLoading = false;
          for(let i = 0; i < this.book_list.length; i++){
            this.book_list[i].cover = require("@/assets/book_images/" + this.book_list[i].book_cover)
          }
        })
        this.uploadFlag = false;
        this.current_book = {};
      },
      showInfo(book){
        //显示信息对话框
        this.infoDialogVisible = true;
        this.current_book = book;
      },
      borrowBook(){
        this.borrowtime = '';
        this.borrowDialogVisible = true;
      },
      borrow(){
        let borrowdate = new Date();
        let returndate = new Date()
        returndate.setDate(borrowdate.getDate() + Number(this.borrowtime));
        this.current_book.borrowerName = this.$store.state.user.nickname;
        this.current_book.borrowTime = borrowdate.format('yyyy-MM-dd');
        this.current_book.returnTime = returndate.format('yyyy-MM-dd');
        this.$confirm("是否借阅该图书","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(()=>{
          this.api({
            url: "/book/borrowBook",
            method: "post",
            data: this.current_book
          }).then(()=>{
            this.infoDialogVisible = false;
            this.borrowDialogVisible = false;
            this.$message({
              type:"success",
              message:"借阅成功！"
            });
            this.getList();
          })
        })
      },
      handleBook(val){
        this.handleDialogVisible = true;
        this.infoDialogVisible = false;
        this.handleFlag = 2;
        this.uploadFlag = true;
        if(val === 1){
          this.current_book = {};
          this.handleFlag = 1;
          this.uploadFlag = false;
        }
      },
      cancelHandle(){
        this.handleDialogVisible = false;
        this.getList();
      },
      addBook(){
        this.api({
          url: "/book/addBook",
          method: "post",
          data: this.current_book
        }).then(()=>{
          this.$refs.upload.submit();
          this.$message({
            type:"success",
            message:"增加成功！"
          })
          this.handleDialogVisible = false;
          this.getList();
        })
      },
      updateBook(){
        this.api({
          url: "/book/updateBook",
          method: "post",
          data: this.current_book
        }).then(()=>{
          this.$message({
            type:"success",
            message:"修改成功！"
          })
          this.handleDialogVisible = false;
          this.getList();
        })
      },
      deleteBook(){
        this.$confirm("是否删除该图书","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(()=>{
          this.api({
            url: "/book/deleteBook",
            method: "post",
            data: this.current_book
          }).then(()=>{
            this.$message({
              type:"success",
              message:"删除成功！"
            })
            this.infoDialogVisible = false;
            this.getList();
          })
        })
      },
      handleCurrentChange(val) {
        //改变页码
        this.pageNum = val;
      },
      // beforeUpload(file){
      //   console.log(file);
      // },
      handleChange(file, fileList){
        let vm = this;
        vm.uploadFlag = false;
        vm.current_book.book_cover = file.name;
        vm.current_book.cover = URL.createObjectURL(file.raw);
        vm.uploadFlag = true;
      }
    }
  }
</script>

<style scoped>
.books{
  display: inline-block;
  margin: 20px 30px 10px;
  min-height: 600px;
}
.item{
  float: left;
  margin: 15px 40px;
  width: 188px;
  background: #fff;
}
.item:hover .item_cover{
  border: 1px solid #ff6100;
}
.item_cover{
  width: 186px;
  height: 220px;
  overflow: hidden;
  cursor: pointer;
}
.item_info{
  display: inline-block;
  width: 180px;
  text-align: center;
  padding: 10px;
}
.book_info .el-form-item{
  margin-bottom: 10px;
}
.page {
  position: relative;
  margin-left: 35%;
}
.handleForm{
  width: 85%;
}
</style>

<style>
.infoDialog .el-dialog{
  position: relative;
  left: -300px;
}
.borrowDialog .el-dialog{
  position: relative;
  left: 400px;
}
.cover-upload .el-upload{
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.cover-upload .el-upload:hover{
  border-color: #409EFF;
}
.cover-upload-icon{
  font-size: 28px;
  color: #8c939d;
  width: 50px;
  height: 50px;
  line-height: 55px;
  text-align: center;
}
.cover-avatar{
  width: 75px;
  height: 95px;
  display: block;
}
</style>
