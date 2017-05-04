/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.config.dsl.parameter;

import static org.mule.runtime.dsl.api.component.AttributeDefinition.Builder.fromFixedValue;
import static org.mule.runtime.dsl.api.component.AttributeDefinition.Builder.fromReferenceObject;
import static org.mule.runtime.dsl.api.component.TypeDefinition.fromType;
import static org.mule.runtime.extension.api.declaration.type.ExtensionsTypeLoaderFactory.getDefault;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.meta.model.parameter.ParameterGroupModel;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.config.ConfigurationException;
import org.mule.runtime.dsl.api.component.ComponentBuildingDefinition;
import org.mule.runtime.extension.api.dsl.syntax.DslElementSyntax;
import org.mule.runtime.extension.api.dsl.syntax.resolver.DslSyntaxResolver;
import org.mule.runtime.module.extension.internal.config.dsl.ExtensionParsingContext;
import org.mule.runtime.module.extension.internal.loader.ParameterGroupDescriptor;
import org.mule.runtime.module.extension.internal.runtime.resolver.ValueResolver;

/**
 * A {@link ParameterGroupParser} which returns the values of the parameters in the group
 * in fields of an object of a given type
 *
 * @since 4.0
 */
public class TypedParameterGroupParser extends ParameterGroupParser {

  private final MetadataType metadataType;

  public TypedParameterGroupParser(ComponentBuildingDefinition.Builder definition,
                                   ParameterGroupModel group,
                                   ParameterGroupDescriptor groupDescriptor,
                                   ClassLoader classLoader, DslElementSyntax groupDsl,
                                   DslSyntaxResolver dslResolver,
                                   ExtensionParsingContext context) {
    super(definition, group, classLoader, groupDsl, dslResolver, context);
    this.metadataType = getDefault().createTypeLoader(classLoader).load(groupDescriptor.getType().getDeclaringClass());
  }


  @Override
  protected void doParse(ComponentBuildingDefinition.Builder definitionBuilder) throws ConfigurationException {
    definitionBuilder.withIdentifier(name).withNamespace(namespace).asNamed().withTypeDefinition(fromType(ValueResolver.class))
        .withObjectFactoryType(TopLevelParameterObjectFactory.class)
        .withConstructorParameterDefinition(fromFixedValue(metadataType).build())
        .withConstructorParameterDefinition(fromFixedValue(classLoader).build())
        .withConstructorParameterDefinition(fromReferenceObject(MuleContext.class).build());

    this.parseParameters(group.getParameterModels());
  }
}