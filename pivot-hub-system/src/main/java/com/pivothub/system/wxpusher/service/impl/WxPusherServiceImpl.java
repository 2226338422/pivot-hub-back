package com.pivothub.system.wxpusher.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.common.util.TLUtil;
import com.pivothub.pojo.po.system.SysUserPlatform;
import com.pivothub.pojo.vo.system.wxpusher.WxPusherQrcodeVo;
import com.pivothub.system.mapper.SysUserPlatformMapper;
import com.pivothub.system.wxpusher.client.WxPusherClient;
import com.pivothub.system.wxpusher.service.WxPusherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** WxPusher 推送绑定与消息发送实现，绑定信息落 sys_user_platform 表。 */
@Service
public class WxPusherServiceImpl implements WxPusherService {
    private static final Logger LOG = LoggerFactory.getLogger(WxPusherServiceImpl.class);

    @Autowired
    private WxPusherClient client;

    @Autowired
    private SysUserPlatformMapper platformMapper;

    @Override
    public WxPusherQrcodeVo qrcode() {
        String userId = currentUserId();
        SysUserPlatform platform = platformMapper.selectByUserId(userId);
        WxPusherQrcodeVo vo = new WxPusherQrcodeVo();
        if (platform != null && Integer.valueOf(1).equals(platform.getWxpusherStatus())
                && platform.getWxpusherUid() != null && !platform.getWxpusherUid().isBlank()) {
            vo.setBound(true);
            return vo;
        }
        // 用用户uuid作为 extra，回调时据此反查用户
        return client.createQrcode(userId);
    }

    @Override
    public boolean bound() {
        String userId = currentUserId();
        SysUserPlatform platform = platformMapper.selectByUserId(userId);
        return platform != null
                && Integer.valueOf(1).equals(platform.getWxpusherStatus())
                && platform.getWxpusherUid() != null && !platform.getWxpusherUid().isBlank();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleSubscribe(String uid, String extra) {
        if (uid == null || uid.isBlank()) {
            throw new BusinessException(400, "回调缺少 uid");
        }
        String userId = extra;
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(400, "回调缺少用户标识");
        }
        // 同一微信号换绑到新用户：旧账号只置未绑定，uid 保留作为历史记录
        SysUserPlatform active = platformMapper.selectActiveByWxpusherUid(uid);
        if (active != null && !userId.equals(active.getUserId())) {
            platformMapper.deactivateByWxpusherUid(uid);
            LOG.info("WxPusher 旧绑定已失效，userId={} uid={}", active.getUserId(), uid);
        }
        // 当前用户已有平台记录则更新，没有则新建一行
        SysUserPlatform platform = platformMapper.selectByUserId(userId);
        if (platform == null) {
            platform = new SysUserPlatform();
            platform.setUuid(IdWorker.getIdStr());
            platform.setUserId(userId);
            platform.setQqStatus(0);
            platform.setWxStatus(0);
            platform.setWxpusherUid(uid);
            platform.setWxpusherStatus(1);
            platform.setMallStatus(0);
            platform.setCreateUserId(userId);
            platform.setUpdateUserId(userId);
            platformMapper.insert(platform);
        } else {
            platform.setWxpusherUid(uid);
            platform.setWxpusherStatus(1);
            platform.setUpdateUserId(userId);
            platformMapper.updateWxpusher(platform);
        }
        LOG.info("WxPusher 绑定成功，userId={} uid={}", userId, uid);
    }

    @Override
    public void unbind() {
        String userId = currentUserId();
        SysUserPlatform platform = platformMapper.selectByUserId(userId);
        if (platform == null || !Integer.valueOf(1).equals(platform.getWxpusherStatus())) {
            throw new BusinessException(400, "当前账号尚未绑定微信通知");
        }
        platformMapper.unbind(userId);
        LOG.info("WxPusher 解绑成功，userId={}", userId);
    }

    private String currentUserId() {
        String userId = TLUtil.get(TLUtil.USER_ID);
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(500, "用户身份上下文不可用");
        }
        return userId;
    }
}
