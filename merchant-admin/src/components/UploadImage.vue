<template>
  <div class="upload-image">
    <el-upload
      class="image-uploader"
      action="http://localhost:8080/api/upload"
      :show-file-list="false"
      :on-success="handleSuccess"
      :before-upload="beforeUpload"
    >
      <img v-if="modelValue" :src="modelValue" class="image-preview" />
      <el-icon v-else class="upload-icon"><Plus /></el-icon>
    </el-upload>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 支持 v-model 绑定：父组件用 <UploadImage v-model="form.cover" />
const props = defineProps({
  modelValue: String
})
const emit = defineEmits(['update:modelValue'])

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) ElMessage.error('只能上传图片文件！')
  if (!isLt2M) ElMessage.error('图片大小不能超过 2MB！')
  return isImage && isLt2M
}

// 上传成功回调
const handleSuccess = (res) => {
  if (res.code === 200) {
    emit('update:modelValue', res.data) // 返回图片路径
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}
</script>

<style scoped>
.image-uploader {
  width: 120px;
  height: 120px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}
.image-uploader:hover {
  border-color: #409eff;
}
.image-preview {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
}
.upload-icon {
  font-size: 28px;
  color: #8c939d;
}
</style>
