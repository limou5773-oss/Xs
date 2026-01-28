<template>
  <div class="page">

    <!-- 返回 -->
    <div class="back-bar" @click="$router.back()">
      <el-icon><ArrowLeft /></el-icon>
      返回公告列表
    </div>

    <h2 class="page-title">{{ isEdit ? "编辑公告" : "发布公告" }}</h2>

    <el-card class="editor-card">

      <el-form label-width="80px">

        <!-- 标题 -->
        <el-form-item label="标题">
          <el-input
            v-model="title"
            placeholder="请输入公告标题"
            clearable
            class="input-lg"
          />
        </el-form-item>

        <!-- 富文本编辑器 -->
        <el-form-item label="内容">
          <div class="editor-wrapper">
            <Toolbar class="editor-toolbar" :editor="editorRef" />
            <Editor
              v-model="content"
              class="editor-content"
              @onCreated="onCreated"
            />
          </div>
        </el-form-item>

        <!-- 提交按钮 -->
        <div class="btn-center">
          <el-button type="primary" size="large" class="btn-primary" @click="submit">
            {{ isEdit ? "保存修改" : "发布" }}
          </el-button>
        </div>

      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { noticeCreate, noticeDetail, noticeUpdate } from "@/api/notice";
import { ElMessage } from "element-plus";
import { ArrowLeft } from "@element-plus/icons-vue";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";

const route = useRoute();
const router = useRouter();

const isEdit = !!route.params.id;

const title = ref("");
const content = ref("");

const editorRef = ref(null);
const onCreated = (editor) => (editorRef.value = editor);

const loadDetail = async () => {
  const res = await noticeDetail(route.params.id);
  title.value = res.data.title;
  content.value = res.data.content;
};

const submit = async () => {
  if (!title.value) return ElMessage.error("标题不能为空");

  if (isEdit) {
    await noticeUpdate({
      id: route.params.id,
      title: title.value,
      content: content.value
    });
    ElMessage.success("修改成功");
  } else {
    await noticeCreate({
      title: title.value,
      content: content.value
    });
    ElMessage.success("发布成功");
  }

  router.push("/notice");
};

onMounted(() => {
  if (isEdit) loadDetail();
});
</script>

<style src="@wangeditor/editor/dist/css/style.css"></style>

<style scoped>
.page {
  padding: 26px 28px;
}

/* 返回栏 */
.back-bar {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: var(--primary);
  margin-bottom: 12px;
  transition: 0.15s;
}

.back-bar:hover {
  color: var(--primary-hover);
  transform: translateX(-3px);
}

.page-title {
  margin-bottom: 20px;
  font-size: 22px;
  font-weight: 600;
}

/* 卡片 */
.editor-card {
  padding: 28px;
  border-radius: 12px;
  box-shadow: var(--shadow-card);
  transition: 0.2s;
}

.editor-card:hover {
  box-shadow: var(--shadow-card-hover);
}

/* 输入框 */
.input-lg {
  height: 40px;
}

/* 编辑器 */
.editor-wrapper {
  border: 1px solid var(--border-color);
  border-radius: 10px;
  overflow: hidden;
}

.editor-toolbar {
  padding: 10px 12px;
  border-bottom: 1px solid #e5e5e5;
}

.editor-content {
  height: 400px !important;
  background: #fafafa;
  padding: 12px;
}

/* 按钮 */
.btn-center {
  margin-top: 26px;
  text-align: center;
}

.btn-primary {
  width: 180px;
  border-radius: 8px;
  transition: 0.15s;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-btn-hover);
}
</style>
