package org.jetbrains.reflekt

import kotlin.reflect.KAnnotatedElement

public interface ReflektAnnotatedElement {
    /**
     * Annotations which are present on this element.
     * An important difference from the annotations retrieved by runtime reflection ([KAnnotatedElement.annotations]) is that instances in this set are not the
     * same references as the ones on the actual classes, so, can't be validly compared by using [Any.equals].
     */
    public val annotations: Set<Annotation>
}
