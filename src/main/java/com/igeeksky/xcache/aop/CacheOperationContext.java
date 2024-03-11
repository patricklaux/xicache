package com.igeeksky.xcache.aop;

import com.igeeksky.xcache.Cache;
import com.igeeksky.xcache.CacheManager;
import com.igeeksky.xcache.annotation.CacheOperation;
import com.igeeksky.xcache.annotation.CacheableAllOperation;
import com.igeeksky.xcache.annotation.CacheableOperation;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xtool.core.collection.CollectionUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * @author patrick
 * @since 0.0.4 2024/2/22
 */
public class CacheOperationContext {

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private CacheManager cacheManager;

    private LinkedMultiValueMap<Class<? extends CacheOperation>, CacheOperation> contexts;

    private MethodInvocation invocation;

    private Object target;

    private Method method;

    private Object[] args;

    /**
     * 记录方法是否已执行，避免重复执行方法：代理的方法仅能执行一次
     */
    private boolean proceed;

    private Object result;

    public CacheOperationContext(Collection<CacheOperation> cacheOperations) {
        this.contexts = new LinkedMultiValueMap<>(cacheOperations.size());
        for (CacheOperation cacheOperation : cacheOperations) {
            this.contexts.add(cacheOperation.getClass(), cacheOperation);
        }
    }

    public CacheOperationContext setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        return this;
    }

    public CacheOperationContext setInvocation(MethodInvocation invocation) {
        this.invocation = invocation;
        return this;
    }

    public CacheOperationContext setTarget(Object target) {
        this.target = target;
        return this;
    }

    public CacheOperationContext setMethod(Method method) {
        this.method = method;
        return this;
    }

    public CacheOperationContext setArgs(Object[] args) {
        this.args = args;
        return this;
    }

    public Object execute() throws Throwable {
        List<CacheOperation> cacheableOperations = contexts.get(CacheableOperation.class);
        if (CollectionUtils.isNotEmpty(cacheableOperations)) {
            for (CacheOperation op : cacheableOperations) {
                cacheable((CacheableOperation) op);
            }
        }

        List<CacheOperation> cacheableAllOperations = contexts.get(CacheableAllOperation.class);
        if (CollectionUtils.isNotEmpty(cacheableAllOperations)) {
            for (CacheOperation op : cacheableAllOperations) {
                cacheableAll((CacheableAllOperation) op);
            }
        }

        return result;
    }

    private void cacheableAll(CacheableAllOperation op) {

    }

    private Mono<?> cacheable(CacheableOperation operation) throws Throwable {
        // 根据注解 condition 判断是否缓存

        // 根据 args 使用 SpEL 生成 key
        Object key = generateKey(operation);

        // 根据注解获取对应的 cache
        String cacheName = operation.getName();

        Cache cache = cacheManager.getOrCreateCache(cacheName, operation.getKeyType(), operation.getValueType(), operation.getValueParams());

        Mono<CacheValue<?>> mono = cache.get(key);

        // 根据注解 unless 判断方法执行结果是否缓存
        return mono.map(CacheValue::getValue);
    }

    private Mono<?> invoke(Object key, Cache cache) throws Throwable {
        Mono<?> mono = (Mono<?>) Objects.requireNonNull(invocation.proceed());
        return mono.doOnSuccess(o -> cache.put(key, Mono.justOrEmpty(o)));
    }

    private Object generateKey(CacheableOperation operation) {
        // 根据 args 使用 SpEL 生成 key
        SpelExpressionParser parser = new SpelExpressionParser();

        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(target, method, args, parameterNameDiscoverer);

        Expression expression = parser.parseExpression(operation.getKey());

        return expression.getValue(context);
    }

    private void doPut() {

    }

}
