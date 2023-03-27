package org.phial.baas.manager.service;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.ognl.*;
import org.apache.ibatis.reflection.Reflector;
import org.mayanjun.core.Assert;
import org.mayanjun.core.ServiceException;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.EditableEntity;
import org.mayanjun.mybatisx.api.entity.Entity;
import org.mayanjun.mybatisx.api.query.Query;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.mayanjun.mybatisx.api.query.SortDirection;
import org.mayanjun.mybatisx.dal.generator.AnnotationHelper;
import org.mayanjun.mybatisx.dal.generator.AnnotationHolder;
import org.mayanjun.mybatisx.dal.util.ClassUtils;
import org.mayanjun.myrest.session.SessionUser;
import org.mayanjun.myrest.session.UserLoader;
import org.phial.baas.manager.config.cache.CacheClient;
import org.phial.baas.manager.config.cache.CacheKey;
import org.phial.baas.manager.config.interceptor.ConsoleSessionManager;
import org.phial.baas.manager.config.interceptor.MobileSessionManager;
import org.phial.baas.manager.service.system.FileBusiness;
import org.phial.baas.manager.util.JsonUtils;
import org.phial.baas.service.domain.entity.NamedEntity;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.phial.baas.service.domain.entity.sys.AbstractUser;
import org.phial.baas.service.listener.system.EntityEventDispatcher;
import org.phial.baas.service.listener.system.EntityEventListener;
import org.phial.baas.service.service.Business;
import org.phial.baas.service.service.EntityEvent;
import org.phial.baas.service.service.ExportIgnore;
import org.phial.baas.service.service.ParametersBuilder;
import org.phial.baas.service.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import javax.annotation.Resource;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;


/**
 * 通用的CRUD处理器。无特殊需求的情况下，子类只需要继承即可。
 *
 * @author mayanjun
 * @vendor mayanjun.org
 * @generator consolegen 1.0
 * @manufacturer https://mayanjun.org
 * @since 2019-10-10
 */
public abstract class ConsoleBaseBusiness<T extends Entity> extends Business implements EntityEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleBaseBusiness.class);

    public static final int PAGE_SIZE = 10;

    private Class<T> beanType = null;

    @Resource
    protected ConsoleSessionManager sessionManager;

    @Resource
    private CacheClient cacheClient;

    @Autowired
    private EntityEventDispatcher dispatcher;
    @Resource
    private ConsoleSessionManager consoleSessionManager;


    public long count(ParametersBuilder parametersBuilder) {
        QueryBuilder<T> builder = renderSearchEngine(parametersBuilder);
        return service.count(builder.build());
    }

    /**
     * 查询实体列表
     *
     * @return
     */
    public List<T> list(int page, int pageSize, ParametersBuilder parametersBuilder) {
        QueryBuilder<T> builder = renderSearchEngine(parametersBuilder);

        // set page
        if (page < 0) page = 1;
        if (pageSize < 0) pageSize = PAGE_SIZE;
        page = (page - 1) * pageSize;
        builder.limit(page, pageSize);

        String orderField = parametersBuilder.getOrderField();
        if (StringUtils.isBlank(orderField) || (!parametersBuilder.isMultipleFieldSort() && !isColumnExists(orderField))) {
            orderField = "id";
        } else if (parametersBuilder.isMultipleFieldSort() && StringUtils.isNotBlank(orderField)) {
            // "id asc,name"
            orderField = orderField;
        } else {
            orderField = "id";
        }
        SortDirection sd = parametersBuilder.getOrderDirection();
        if (sd == null) sd = SortDirection.DESC;

        builder.orderBy(orderField, sd);
        List<T> list = doQuery(builder);
        afterQueryList(list);
        return list;
    }

    protected void afterQueryList(List<T> list) {

    }

//    public FileMeta exportData(ParametersBuilder parametersBuilder) {
//        try {
//            QueryBuilder<T> builder = renderSearchEngine(parametersBuilder);
//            builder.orderBy("id", SortDirection.DESC);
//            List<T> list = doQuery(builder);
//            Assert.notEmpty(list, "暂无数据需要导出");
//            String fileName = exportFileName(parametersBuilder);
//            File localFile = fileBusiness.localFile(fileName);
//            // 写Excel
//            XSSFWorkbook workbook = new XSSFWorkbook();
//            XSSFCellStyle cellStyle = workbook.createCellStyle();
//
//            XSSFCellStyle headerStyle = workbook.createCellStyle();
//            headerStyle.setBorderBottom(BorderStyle.DOUBLE);
//            headerStyle.setBottomBorderColor(new XSSFColor(new Color(100, 100, 100)));
//            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            headerStyle.setFillForegroundColor(new XSSFColor(new Color(197, 232, 239)));
//
//
//            XSSFCellStyle dataStyle = workbook.createCellStyle();
//            dataStyle.setBorderBottom(BorderStyle.THIN);
//            dataStyle.setBottomBorderColor(new XSSFColor(new Color(180, 180, 180)));
//            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//
//            XSSFDataFormat format = workbook.createDataFormat();
//            cellStyle.setDataFormat(format.getFormat("yyyy-MM-dd hh:mm:ss"));
//
//            XSSFSheet spreadsheet = workbook.createSheet("数据表");
//            String headers[] = formatExportEntityHeaders(beanType);
//            if (headers != null && headers.length > 0) {
//                XSSFRow row = spreadsheet.createRow(0);
//                row.setHeight((short) (256 * 2));
//                for (int i = 0; i < headers.length; i++) {
//                    XSSFCell cell = row.createCell(i);
//                    cell.setCellValue(headers[i]);
//                    spreadsheet.setColumnWidth(i, 256 * 20 + 184);
//                    cell.setCellStyle(headerStyle);
//                }
//            }
//
//            int count = 1;
//            for (T e : list) {
//                XSSFRow row = spreadsheet.createRow(count++);
//
//                row.setHeight((short) (256 * 1.5));
//                String values[] = formatExportEntity(e);
//                if (values != null && values.length > 0) {
//                    for (int i = 0; i < values.length; i++) {
//                        XSSFCell cell = row.createCell(i);
//                        cell.setCellValue(values[i]);
//                        cell.setCellStyle(dataStyle);
//                    }
//                }
//            }
//
//            OutputStream out = new FileOutputStream(localFile);
//            workbook.write(out);
//            out.close();
//            workbook.close();
//            LOG.info("Dara export done: file={}", fileName);
//
//            try {
//                return fileBusiness.saveFileMeta(localFile, "ExportData:" + this.getBeanType().getSimpleName());
//            } catch (Exception e) {
//                LOG.warn("Save export data file error", e);
//            }
//        } catch (ServiceException e) {
//            throw e;
//        } catch (Exception e) {
//            LOG.error("Export file error", e);
//            throw new ServiceException("文件导出失败");
//        }
//
//        return null;
//    }

//    public FileMeta exportData(ParametersBuilder parametersBuilder, Long[] ids) {
//        try {
//            QueryBuilder<T> builder = renderSearchEngine(parametersBuilder);
//            if (null != ids && ids.length > 0) {
//                builder.andIn("id", ids);
//            }
//            builder.orderBy("id", SortDirection.DESC);
//            List<T> list = doQuery(builder);
//            Assert.notEmpty(list, "暂无数据需要导出");
//            String fileName = exportFileName(parametersBuilder);
//            File localFile = fileBusiness.localFile(fileName);
//            // 写Excel
//            XSSFWorkbook workbook = new XSSFWorkbook();
//            XSSFCellStyle cellStyle = workbook.createCellStyle();
//
//            XSSFCellStyle headerStyle = workbook.createCellStyle();
//            headerStyle.setBorderBottom(BorderStyle.DOUBLE);
//            headerStyle.setBottomBorderColor(new XSSFColor(new Color(100, 100, 100)));
//            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            headerStyle.setFillForegroundColor(new XSSFColor(new Color(197, 232, 239)));
//
//
//            XSSFCellStyle dataStyle = workbook.createCellStyle();
//            dataStyle.setBorderBottom(BorderStyle.THIN);
//            dataStyle.setBottomBorderColor(new XSSFColor(new Color(180, 180, 180)));
//            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//
//            XSSFDataFormat format = workbook.createDataFormat();
//            cellStyle.setDataFormat(format.getFormat("yyyy-MM-dd hh:mm:ss"));
//
//            XSSFSheet spreadsheet = workbook.createSheet("数据表");
//            String headers[] = formatExportEntityHeaders(beanType);
//            if (headers != null && headers.length > 0) {
//                XSSFRow row = spreadsheet.createRow(0);
//                row.setHeight((short) (256 * 2));
//                for (int i = 0; i < headers.length; i++) {
//                    XSSFCell cell = row.createCell(i);
//                    cell.setCellValue(headers[i]);
//                    spreadsheet.setColumnWidth(i, 256 * 20 + 184);
//                    cell.setCellStyle(headerStyle);
//                }
//            }
//
//            int count = 1;
//            for (T e : list) {
//                XSSFRow row = spreadsheet.createRow(count++);
//
//                row.setHeight((short) (256 * 1.5));
//                String values[] = formatExportEntity(e);
//                if (values != null && values.length > 0) {
//                    for (int i = 0; i < values.length; i++) {
//                        XSSFCell cell = row.createCell(i);
//                        cell.setCellValue(values[i]);
//                        cell.setCellStyle(dataStyle);
//                    }
//                }
//            }
//
//            OutputStream out = new FileOutputStream(localFile);
//            workbook.write(out);
//            out.close();
//            workbook.close();
//            LOG.info("Dara export done: file={}", fileName);
//
//            try {
//                return fileBusiness.saveFileMeta(localFile, "ExportData:" + this.getBeanType().getSimpleName());
//            } catch (Exception e) {
//                LOG.warn("Save export data file error", e);
//            }
//        } catch (ServiceException e) {
//            throw e;
//        } catch (Exception e) {
//            LOG.error("Export file error", e);
//            throw new ServiceException("文件导出失败");
//        }
//
//        return null;
//    }


//    public FileMeta exportDataLimit(ParametersBuilder parametersBuilder, Long[] ids, int page, int pageSize ,WorkTaskType workTaskType) {
//        try {
//            QueryBuilder<T> builder = renderSearchEngine(parametersBuilder);
//            if (null != ids && ids.length > 0) {
//                builder.andIn("id", ids);
//            }else{
//                if (page < 0) page = 1;
//                if (pageSize < 0) pageSize = PAGE_SIZE;
//                page = (page - 1) * pageSize;
//                builder.limit(page, pageSize);
//            }
//            builder.orderBy("id", SortDirection.DESC);
//            List<T> list = doQuery(builder);
//            Assert.notEmpty(list, "暂无数据需要导出");
//            String fileName = exportFileName(parametersBuilder,workTaskType.getDisplayName());
//            File localFile = fileBusiness.localFile(fileName);
//            // 写Excel
//            XSSFWorkbook workbook = new XSSFWorkbook();
//            XSSFCellStyle cellStyle = workbook.createCellStyle();
//
//            XSSFCellStyle headerStyle = workbook.createCellStyle();
//            headerStyle.setBorderBottom(BorderStyle.DOUBLE);
//            headerStyle.setBottomBorderColor(new XSSFColor(new Color(100, 100, 100)));
//            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            headerStyle.setFillForegroundColor(new XSSFColor(new Color(197, 232, 239)));
//
//
//            XSSFCellStyle dataStyle = workbook.createCellStyle();
//            dataStyle.setBorderBottom(BorderStyle.THIN);
//            dataStyle.setBottomBorderColor(new XSSFColor(new Color(180, 180, 180)));
//            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//
//            XSSFDataFormat format = workbook.createDataFormat();
//            cellStyle.setDataFormat(format.getFormat("yyyy-MM-dd hh:mm:ss"));
//
//            XSSFSheet spreadsheet = workbook.createSheet("数据表");
//            String headers[] = formatExportEntityHeaders(beanType);
//            if (headers != null && headers.length > 0) {
//                XSSFRow row = spreadsheet.createRow(0);
//                row.setHeight((short) (256 * 2));
//                for (int i = 0; i < headers.length; i++) {
//                    XSSFCell cell = row.createCell(i);
//                    cell.setCellValue(headers[i]);
//                    spreadsheet.setColumnWidth(i, 256 * 20 + 184);
//                    cell.setCellStyle(headerStyle);
//                }
//            }
//
//            int count = 1;
//            for (T e : list) {
//                XSSFRow row = spreadsheet.createRow(count++);
//
//                row.setHeight((short) (256 * 1.5));
//                String values[] = formatExportEntity(e);
//                if (values != null && values.length > 0) {
//                    for (int i = 0; i < values.length; i++) {
//                        XSSFCell cell = row.createCell(i);
//                        cell.setCellValue(values[i]);
//                        cell.setCellStyle(dataStyle);
//                    }
//                }
//            }
//
//            OutputStream out = new FileOutputStream(localFile);
//            workbook.write(out);
//            out.close();
//            workbook.close();
//            LOG.info("Dara export done: file={}", fileName);
//
//            try {
//                return fileBusiness.saveFileMeta(localFile, "ExportData:" + this.getBeanType().getSimpleName());
//            } catch (Exception e) {
//                LOG.warn("Save export data file error", e);
//            }
//        } catch (ServiceException e) {
//            throw e;
//        } catch (Exception e) {
//            LOG.error("Export file error", e);
//            throw new ServiceException("文件导出失败");
//        }
//
//        return null;
//    }

    /**
     * 返回导出文件的文件名
     *
     * @param parametersBuilder
     * @return
     */
    protected String exportFileName(ParametersBuilder parametersBuilder) {
        String mn = null;
        Class<T> beanType = getBeanType();

        if (beanType != null) {
            Table table = beanType.getAnnotation(Table.class);
            if (table != null) {
                mn = table.comment();
            }
        }

        String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        if (StringUtils.isBlank(mn)) {
            mn = "文件导出";
        }

        return String.format("%s-%s.xlsx", mn, ts);
    }

    /**
     * 返回指定导出文件名
     *
     * @param parametersBuilder
     * @param name              导出指定文件名字符
     * @return
     */
    protected String exportFileName(ParametersBuilder parametersBuilder, String name) {
        String mn = null;
        Class<T> beanType = getBeanType();

        if (beanType != null) {
            Table table = beanType.getAnnotation(Table.class);
            if (table != null) {
                mn = table.comment();
            }
        }

        String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        if (StringUtils.isBlank(mn)) {
            mn = "文件导出";
        }
        if (StringUtils.isBlank(name)) {
            return String.format("%s-%s.xlsx", mn, ts);
        } else {
            return String.format("%s-%s-%s.xlsx", mn, name, ts);
        }

    }

    private static final MemberAccess OGNL_MEMBER_ACCESS = new DefaultOgnlMemberAccess();


    protected String[] formatExportEntity(T entity) {
        List<AnnotationHolder> holders = AnnotationHelper.getAnnotationHolders(beanType);
        if (holders != null && !holders.isEmpty()) {
            String fs[] = new String[holders.size()];
            List<String> fields = new LinkedList<>();
            holders.stream().forEach(e -> {
                if (!isExportIgnored(e)) {
                    String value = "";
                    try {
                        //value = BeanUtilsBean2.getInstance().getProperty(entity, e.getField().getName());
                        OgnlContext context = new OgnlContext((ClassResolver) null, (TypeConverter) null, OGNL_MEMBER_ACCESS);

                        String ognl = e.getField().getName();
                        if (e.getOgnl() != null) ognl = e.getOgnl() + "." + ognl;

                        Object vo = Ognl.getValue(ognl, context, entity);

                        if (vo != null) value = Strings.convert(vo);
                    } catch (Exception ex) {
                        LOG.error("Export data: get bean entity value error", e);
                        //value = BeanUtilsBean2.getInstance().getProperty(entity, e.get e.getField().getName());
                    }

                    if (value == null) value = "";
                    fields.add(value);
                }
            });
            return fields.toArray(fs);
        }
        return null;
    }

    protected String[] formatExportEntityHeaders(Class<T> beanType) {
        List<AnnotationHolder> holders = AnnotationHelper.getAnnotationHolders(beanType);

        if (holders != null && !holders.isEmpty()) {
            String fs[] = new String[holders.size()];
            List<String> fields = new LinkedList<>();
            holders.stream().forEach(e -> {
                if (!isExportIgnored(e)) {
                    String s = e.getColumn().comment();
                    if (s == null) {
                        s = "";
                    }
                    // 对从框架击继承的字段进行翻译
                    switch (s) {
                        case "Created Time":
                            s = "创建时间";
                            break;
                        case "Last Modified Time":
                            s = "最后更新时间";
                            break;
                        case "Creator":
                            s = "创建人";
                            break;
                        case "Last editor":
                            s = "修改人";
                            break;
                    }
                    fields.add(s);
                }
            });
            return fields.toArray(fs);
        }
        return null;
    }

    /**
     * 返回 true 表示是需要过滤的字段
     *
     * @param holder
     * @return
     */
    public boolean isExportIgnored(AnnotationHolder holder) {
        Field field = holder.getField();
        String fieldName = field.getName();
        if ("creator".equals(fieldName) || "editor".equals(fieldName)) {
            return true;
        }

        ExportIgnore ei = field.getAnnotation(ExportIgnore.class);
        return ei != null;
    }

    protected void renderListAllBuilder(QueryBuilder<T> builder) {
        builder.orderBy("id", SortDirection.DESC);
    }

    public List<T> listAll(ParametersBuilder parametersBuilder) {
        QueryBuilder<T> builder = renderSearchEngine(parametersBuilder);
        renderListAllBuilder(builder);
        return doQuery(builder);
    }

    protected T newInstance(Long id) {
        Class<T> t = getBeanType();
        try {
            T bean = t.getConstructor(Long.class).newInstance(id);
            return bean;
        } catch (Exception e) {
            LOG.error("Can not create instance: " + t, e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 对外提供通过ID查询实体服务
     *
     * @param id
     * @return
     */
    public T get(long id) {
        T bean = newInstance(id);
        return doGet(bean);
    }

    public T fastGet(Long id) {
        if (id == null || id <= 0) return null;
        T ent = cache.get(getBeanType(), id);
        if (ent == null) {
            ent = doGet(newInstance(id));
        }
        return ent;
    }

    protected T doGet(T bean) {
        return service.getExclude(bean);
    }

    /**
     * 获取实际参数类型
     *
     * @return
     */
    protected Class<T> getBeanType() {
        if (this.beanType != null) return beanType;
        beanType = (Class<T>) ClassUtils.getFirstParameterizedType(this.getClass());
        return beanType;
    }

    /**
     * 对外删除实体服务
     *
     * @param ids
     */
    public void delete(Long ids[]) {
        // 逐出缓存
        Query<T> query1 = QueryBuilder.custom(getBeanType()).andIn("id", ids).build();
        List<T> list = service.query(query1);

        Query<T> query2 = QueryBuilder.custom(getBeanType()).andIn("id", ids).build();
        service.delete(query2);
        if (!list.isEmpty()) {
            Entity es[] = new Entity[list.size()];
            list.toArray(es);
            emitEvent(
                    new EntityEvent(EntityEvent.EventType.DELETE, es)
            );
        }
    }

    /**
     * 处理真正的保存逻辑
     *
     * @param bean
     * @return
     */
    protected long doSave(T bean) {
        service.save(bean);
        return (long) bean.getId();
    }

    private long doUniqueSave(T bean, String msg) {
        try {
            return doSave(bean);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(msg);
        }
    }

    /**
     * 处理真正的更新逻辑
     *
     * @param bean
     * @return
     */
    protected int doUpdate(T bean) {
        return service.update(bean);
    }

    /**
     * 处理对外的保存请求，标准的模板方法
     * <p>
     * 模板方法：调用{@link #validate(Entity, boolean)}} (PersistableEntity, boolean)} 和 {@link #doSave(Entity)} 方法
     * </p>
     *
     * @param bean
     */
    public T save(T bean) {
        validate(bean, false);
        long id = doSave(bean);
        LOG.info("Save bean done:id={},class={}", id, bean.getClass().getSimpleName());
        Assert.isTrue(id > 0, "save fail.");
        emitEvent(new EntityEvent(EntityEvent.EventType.NEW, bean));
        return bean;
    }

    public T saveUnique(T bean, String msg) {
        validate(bean, false);
        long id = doUniqueSave(bean, msg);
        LOG.info("Save bean done:id={},class={}", id, bean.getClass().getSimpleName());
        Assert.isTrue(id > 0, "saveUnique fail.");
        emitEvent(new EntityEvent(EntityEvent.EventType.NEW, bean));
        return bean;
    }

    /**
     * 处理对外的更新请求，标准的模板方法
     *
     * @param bean
     */
    public void update(T bean) {
        validate(bean, true);
        int ret = doUpdate(bean);
        LOG.info("Update bean done:ret={},id={},class={}", ret, bean.getClass().getSimpleName(), bean.getId());
        Assert.isTrue(ret > 0, "update fail");

        // 发布更新事件
        emitEvent(new EntityEvent(EntityEvent.EventType.UPDATE, bean));
    }

    /**
     * 设置操作人
     *
     * @param bean
     * @param update
     */
    public void setOperator(Entity bean, boolean update) {
        if (bean instanceof EditableEntity) {
            SessionUser<SysUser> user = sessionManager.getCurrentUser();
            if (user != null) {
                String username = user.getUsername();
                if (update) {
                    ((EditableEntity) bean).setCreator(null);
                } else {
                    ((EditableEntity) bean).setCreator(username);
                }
                ((EditableEntity) bean).setEditor(username);
            }
        }
    }

    /**
     * 填充搜索引擎参数，默认实现，如果有特殊情况，请自行实现
     */
    protected QueryBuilder<T> renderSearchEngine(ParametersBuilder parametersBuilder) {
        QueryBuilder<T> builder = QueryBuilder.custom(getBeanType());

        if (parametersBuilder == null) return builder;

        List<ParametersBuilder.KVEntity> kvEntityList = parametersBuilder.build();
        int invalid = 0;
        for (ParametersBuilder.KVEntity kvEntity : kvEntityList) {
            String name = kvEntity.key;
            Object value = kvEntity.value;
            boolean valid = false;

            int oper = 0;
            if (name.startsWith("__LIKE__")) {
                name = name.substring(8);
                oper = 1;

            } else if (name.startsWith("__<=__")) {
                oper = 2;
                name = name.substring(6);
            } else if (name.startsWith("__>=__")) {
                oper = 3;
                name = name.substring(6);
            } else if (name.startsWith("__>__")) {
                oper = 4;
                name = name.substring(5);
            } else if (name.startsWith("__<__")) {
                oper = 5;
                name = name.substring(5);
            } else if (name.startsWith("__!=__")) {
                oper = 6;
                name = name.substring(6);
            } else if (name.startsWith("__IN__")) {
                oper = 7;
                name = name.substring(6);
            } else if (name.startsWith("__OR__")) {
                oper = 8;
                name = name.substring(6);
            } else if (name.startsWith("__OR_IN__")) {
                oper = 10;
                name = name.substring(9);
            } else if (name.startsWith("__OR_GROUP_START__")) {
                oper = 1000;
                name = name.substring(18);
            } else if (name.startsWith("__AND_GROUP_START__")) {
                oper = 1001;
                name = name.substring(19);
            } else if (name.startsWith("__GROUP_END__")) {
                oper = 1002;
                name = name.substring(13);
            } else if (name.startsWith("__NOT_IN__")) {
                oper = 9;
                name = name.substring(10);
            }

            if (value instanceof String) {
                if (StringUtils.isBlank((String) value)) value = null;
            }

            if (value != null && isColumnExists(name)) {
                valid = true;
                switch (oper) {
                    case 1:
                        builder.andLike(name, "%" + value + "%");
                        break;
                    case 2:
                        builder.andLessThan(name, value, true);
                        break;
                    case 3:
                        builder.andGreaterThan(name, value, true);
                        break;
                    case 4:
                        builder.andGreaterThan(name, value);
                        break;
                    case 5:
                        builder.andLessThan(name, value);
                        break;
                    case 6:
                        builder.andNotEquivalent(name, value);
                        break;
                    case 7:
                        if (value instanceof Collection) {
                            Object[] objects = ((Collection) value).toArray();
                            builder.andIn(name, objects);
                        } else if (value instanceof Object[]) {
                            builder.andIn(name, (Object[]) value);
                        }
                        break;
                    case 8:
                        builder.orEquivalent(name, value);
                        break;
                    case 10:
                        if (value instanceof Collection) {
                            Object[] objects = ((Collection) value).toArray();
                            builder.orIn(name, objects);
                        } else if (value instanceof Object[]) {
                            builder.orIn(name, (Object[]) value);
                        }
                        break;
                    case 1000:
                        builder.orGroup();
                        break;
                    case 1001:
                        builder.andGroup();
                        break;
                    case 1002:
                        builder.endGroup();
                        break;
                    case 9:
                        if (value instanceof Collection) {
                            Object[] objects = ((Collection) value).toArray();
                            builder.andNotIn(name, objects);
                        } else if (value instanceof Object[]) {
                            builder.andNotIn(name, (Object[]) value);
                        }
                        break;
                    default:
                        builder.andEquivalent(name, value);
                }
            } else {
                ++invalid;
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Search field is ignored: name={}, value={}", name, value);
                }
            }
            if (parametersBuilder.isChainedDepend()) {
                if (valid && invalid > 0) throw new ServiceException("该查询条件必须遵守最左填充原则");
            }
        }

        String ef[] = parametersBuilder.getExcludeFields();
        if (ef != null && ef.length > 0) {
            builder.excludeFields(ef);
        }

        return builder;
    }

    /**
     * 检测一个字段列是否存在
     *
     * @param name
     * @return
     */
    private boolean isColumnExists(String name) {
        AnnotationHolder holder = AnnotationHelper.getAnnotationHolder(name, getBeanType());
        return holder != null;
    }

    /**
     * 子类可以实现查询逻辑
     *
     * @param builder
     * @return
     */
    protected List<T> doQuery(QueryBuilder<T> builder) {
        return doQuery(builder.build());
    }


    /**
     * 子类可以实现查询逻辑
     *
     * @param query
     * @return
     */
    protected List<T> doQuery(Query<T> query) {
        return service.query(query);
    }

    /**
     * 执行实体检查操作
     *
     * @param entity
     */
    protected void validate(T entity, boolean update) {
        doCheck(entity, update);
        named(entity, update);
        setOperator(entity, update);
    }

    /**
     * 执行参数逻辑校验检查工作
     *
     * @param entity
     */
    protected void doCheck(T entity, boolean update) {
    }

    protected void checkNamedEntity(T entity, boolean update) {
        if (entity instanceof NamedEntity) {
            NamedEntity ne = (NamedEntity) entity;
            Assert.notBlank(ne.getName(), "名称不能为空");
        }
    }

    protected ConsoleBaseBusiness<T> emitEvent(EntityEvent event) {
        dispatcher.emitEvent(event);
        return this;
    }

    @Override
    public void onEntityChange(EntityEvent event) {
        switch (event.type()) {
            case UPDATE:
            case DELETE:
                evict(event);
                break;
        }
        clearMobileUser(event);
    }


    protected void evict(EntityEvent event) {
        Entity es[] = event.entities();
        if (es != null && es.length > 0) {
            for (Entity e : es) {
                cache.evict(e);
            }
        }
    }

    public static class DefaultOgnlMemberAccess implements MemberAccess {
        private final boolean canControlMemberAccessible = Reflector.canControlMemberAccessible();

        public DefaultOgnlMemberAccess() {
        }

        public Object setup(Map context, Object target, Member member, String propertyName) {
            Object result = null;
            if (this.isAccessible(context, target, member, propertyName)) {
                AccessibleObject accessible = (AccessibleObject) member;
                if (!accessible.isAccessible()) {
                    result = Boolean.FALSE;
                    accessible.setAccessible(true);
                }
            }

            return result;
        }

        public void restore(Map context, Object target, Member member, String propertyName, Object state) {
        }

        public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
            return this.canControlMemberAccessible;
        }
    }

    @Override
    public boolean support(EntityEvent event) {
        Entity[] entities = event.entities();
        if (entities != null && entities.length > 0) {
            return getBeanType() == entities[0].getClass();
        }
        return false;
    }


    public void clearMobileUser(EntityEvent event) {
        if (event.type() == EntityEvent.EventType.UPDATE && event.isNotEmpty()) {
            for (Entity entity : event.getEntities()) {
                if (SysUser.class.equals(event.getTargetClazz())) {
                    SysUser user = (SysUser) entity;
//                    cacheClient.delete(CacheKey.CONSOLE_USER, user.getId().toString());
//                    cacheClient.delete(CacheKey.CONSOLE_USER, user.getUsername());
                    UserLoader<SysUser> userUserLoader = consoleSessionManager.getUserLoader();
                    SessionUser<SysUser> loadUser = userUserLoader.loadUser(user.getUsername());

                    cacheClient.set(CacheKey.CONSOLE_USER, user.getId().toString(), loadUser);
                    cacheClient.set(CacheKey.CONSOLE_USER, user.getUsername(), loadUser);
                }
            }
        }
    }

    private static <T extends AbstractUser> SessionUser<T> createUser(T user) {
        SessionUser<T> sessionUser = new SessionUser<>(user.getUsername());
        sessionUser.setId(user.getId());
        sessionUser.setPassword(user.getPassword());
        sessionUser.setOriginUser(user);
        sessionUser.setLastLoginTime(System.currentTimeMillis());
        return sessionUser;
    }

    /**
     * 获取一条记录
     *
     * @param parametersBuilder
     * @return
     */
    public T queryOne(ParametersBuilder parametersBuilder) {
        QueryBuilder<T> builder = renderSearchEngine(parametersBuilder);
        renderListAllBuilder(builder);
        List<T> result = doQuery(builder);
        if (ObjectUtils.isNotEmpty(result)) {
            return result.get(0);
        }
        return null;
    }

    /**
     * 获取一条记录
     *
     * @param parametersBuilder
     * @return
     */
    public T limitOne(ParametersBuilder parametersBuilder) {
        QueryBuilder<T> builder = renderSearchEngine(parametersBuilder);
        renderListAllBuilder(builder);
        builder.limit(0, 1);
        List<T> result = doQuery(builder);
        if (ObjectUtils.isNotEmpty(result)) {
            return result.get(0);
        }
        return null;
    }
}
