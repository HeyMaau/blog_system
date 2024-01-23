package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IAudioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/portal/audio")
public class AudioPortalApi {

    @Autowired
    private IAudioService audioService;

    @GetMapping("/list")
    public ResponseResult getAudioList(@RequestParam("page") int page, @RequestParam("size") int size) {
        return audioService.getAudioList(page, size);
    }
}
