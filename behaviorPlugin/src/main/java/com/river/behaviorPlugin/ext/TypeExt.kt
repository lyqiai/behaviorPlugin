import org.objectweb.asm.Type

/**
 *@Author: River
 *@Emial: 1632958163@qq.com
 *@Create: 2022/2/11
 *
 **/
fun Type.autoPackage(): Type {
    /**
     * 将基础类型转换为包装类
     */
    if (this == Type.INT_TYPE) {
        return Type.getType(Integer::class.java)
    } else if (this == Type.SHORT_TYPE) {
        return Type.getType(java.lang.Short::class.java)
    } else if (this == Type.BOOLEAN_TYPE) {
        return Type.getType(java.lang.Boolean::class.java)
    } else if (this == Type.LONG_TYPE) {
        return Type.getType(java.lang.Long::class.java)
    } else if (this == Type.BYTE_TYPE) {
        return Type.getType(java.lang.Byte::class.java)
    } else if (this == Type.DOUBLE_TYPE) {
        return Type.getType(java.lang.Double::class.java)
    } else if (this == Type.FLOAT_TYPE) {
        return Type.getType(java.lang.Float::class.java)
    }
    return this
}