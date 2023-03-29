package org.phial.baas.manager.service.system;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import org.apache.commons.lang3.StringUtils;
import org.mayanjun.core.Assert;
import org.mayanjun.mybatisx.api.query.Query;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.mayanjun.mybatisx.api.query.SortDirection;
import org.phial.baas.service.domain.entity.system.Region;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

import static net.sourceforge.pinyin4j.format.HanyuPinyinToneType.WITHOUT_TONE;
import static net.sourceforge.pinyin4j.format.HanyuPinyinVCharType.WITH_V;

/**
 * 地区管理
 * @since 2019-07-06
 * @author mayanjun
 */
@Component
public class RegionBusiness extends ConsoleBaseBusiness<Region> {

    private static final Logger LOG = LoggerFactory.getLogger(RegionBusiness.class);
    private static final HanyuPinyinOutputFormat HANYUPINYINFORMAT = new HanyuPinyinOutputFormat();

    static {
        HANYUPINYINFORMAT.setVCharType(WITH_V);
        HANYUPINYINFORMAT.setToneType(WITHOUT_TONE);
    }

    /**
     * 获取城市列表
     * @return
     */
    public Collection<Region> cities() {
        List<Region> regions = service.query(QueryBuilder.custom(Region.class)
                .andIn("level", new Integer[]{1,2})
                .orderBy("pinyin")
                .includeFields("id", "name", "parent", "level")
                .build());
        // 整理 region
        Map<Long, Region> map = new LinkedHashMap<>();
        Iterator<Region> iterator = regions.iterator();
        while (iterator.hasNext()) {
            Region region = iterator.next();
            if (region.getLevel() == 1) {
                map.put(region.getId(), region);
                iterator.remove();
            }
        }
        regions.forEach(e -> {
            Region parent = map.get(e.getParent().getId());
            if (parent != null) {
                List<Region> children = parent.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    parent.setChildren(children);
                }
                children.add(e);
            }
        });
        return map.values();
    }

    @Override
    protected void doCheck(Region entity, boolean update) {
        Assert.notBlank(entity.getName(), "名称不能为空");
        Assert.notBlank(entity.getShortName(), "短名称不能为空");
        Assert.notNull(entity.getParent(), "父级不能为空");

        if (StringUtils.isNotBlank(entity.getShortName())) {
            StringBuffer sb = new StringBuffer();
            char cs[] = entity.getShortName().toCharArray();
            for (char c : cs) {
                try {
                    String pinyin[] = PinyinHelper.toHanyuPinyinStringArray(c, HANYUPINYINFORMAT);
                    if (pinyin != null && pinyin.length > 0) sb.append(pinyin[0]);
                } catch (Exception e) {
                    LOG.info("Convert pinyin error: " + c, e);
                }
            }
            if (sb.length() > 0) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
            entity.setPinyin(sb.toString());
        }
    }

    @Override
    protected void renderListAllBuilder(QueryBuilder<Region> builder) {
        builder.orderBy("pinyin", SortDirection.ASC);
    }

    @Override
    public void delete(Long[] ids) {
        transaction().execute(transactionStatus -> {
            Set<Long> idset = new HashSet<>();
            for (Long id : ids) {
                loadTree(idset, id);
            }
            idset.stream().forEach(e -> {
                Region region = new Region(e);
                service.delete(region);
            });
            LOG.info("Region delete done: size={}", idset.size());
            return 0;
        });
    }

    private void loadTree(Set<Long> set, Long id) {
        if (set.contains(id)) return;
        set.add(id);
        Query<Region> query = QueryBuilder.custom(Region.class)
                .andEquivalent("parent", id)
                .includeFields("id")
                .build();
        List<Region> regions = service.query(query);
        regions.stream().forEach(e -> loadTree(set, e.getId()));
    }
}
