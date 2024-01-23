package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogAudio;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IAudioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/audio")
@PreAuthorize("@permission.admin")
public class AudioAdminApi {

    @Autowired
    private IAudioService audioService;

    @PostMapping("/add")
    public ResponseResult addAudio(@RequestBody BlogAudio blogAudio) {
        log.info("管理平台添加音频 ----> " + blogAudio);
        return audioService.addAudio(blogAudio);
    }

    @PutMapping("/update")
    public ResponseResult updateAudio(@RequestBody BlogAudio blogAudio) {
        log.info("管理平台修改音频 ----> " + blogAudio);
        return audioService.updateAudio(blogAudio);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteAudio(@PathVariable("id") String id) {
        return audioService.deleteAudio(id);
    }
}
