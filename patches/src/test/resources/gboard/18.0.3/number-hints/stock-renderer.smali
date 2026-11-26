.class public Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;
.super Landroid/widget/FrameLayout;
.field public e:Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;

.method private final x(Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;)V
    .locals 16

    move-object/from16 v0, p0

    move-object/from16 v1, p1

    iget-object v2, v0, Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;->e:Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;

    const/16 v4, 0x8

    const/4 v5, 0x0

    if-eqz v1, :cond_4

    iget-object v6, v1, Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;->j:[I

    array-length v7, v6

    move v8, v5

    :goto_0
    if-ge v8, v7, :cond_4

    aget v9, v6, v8

    iget-object v10, v2, Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;->j:[I

    array-length v11, v10

    move v12, v5

    :goto_1
    if-ge v12, v11, :cond_1

    aget v13, v10, v12

    if-ne v13, v9, :cond_0

    goto :goto_2

    :cond_0
    add-int/lit8 v12, v12, 0x1

    goto :goto_1

    :cond_1
    if-nez v9, :cond_2

    const v9, 0x7f0b03e3

    :cond_2
    invoke-virtual {v0, v9}, Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;->findViewById(I)Landroid/view/View;

    move-result-object v9

    check-cast v9, Landroid/widget/ImageView;

    if-eqz v9, :cond_3

    const/4 v10, 0x0

    invoke-virtual {v9, v10}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    invoke-virtual {v9, v4}, Landroid/widget/ImageView;->setVisibility(I)V

    iget-object v10, v0, Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;->s:Laxt;

    invoke-virtual {v10, v9}, Lays;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v9

    check-cast v9, Logo;

    if-eqz v9, :cond_3

    invoke-virtual {v9}, Logo;->p()V

    :cond_3
    :goto_2
    add-int/lit8 v8, v8, 0x1

    goto :goto_0

    :cond_4
    iget-object v6, v2, Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;->i:[Ljava/lang/Object;

    iget-object v7, v2, Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;->j:[I

    array-length v8, v6

    move v9, v5

    :goto_3
    const/4 v10, 0x2

    if-ge v9, v8, :cond_d

    aget v11, v7, v9

    if-nez v11, :cond_5

    const v11, 0x7f0b03e3

    :cond_5
    invoke-virtual {v0, v11}, Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;->findViewById(I)Landroid/view/View;

    move-result-object v11

    check-cast v11, Landroid/widget/ImageView;

    aget-object v12, v6, v9

    if-nez v11, :cond_6

    goto/16 :goto_6

    :cond_6
    iget-object v13, v0, Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;->s:Laxt;

    invoke-virtual {v13, v11}, Lays;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v14

    check-cast v14, Logo;

    const/4 v15, 0x1

    if-nez v14, :cond_7

    instance-of v14, v11, Lcom/google/android/libraries/inputmethod/accesspoint/impl/AnimatedAccessPointEntryIconView;

    xor-int/2addr v14, v15

    new-instance v3, Logo;

    invoke-direct {v3, v11, v14}, Logo;-><init>(Landroid/widget/ImageView;Z)V

    invoke-virtual {v13, v11, v3}, Lays;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-object v14, v3

    :cond_7
    instance-of v3, v12, Ljava/lang/Integer;

    if-eqz v3, :cond_9

    check-cast v12, Ljava/lang/Integer;

    invoke-virtual {v12}, Ljava/lang/Integer;->intValue()I

    move-result v3

    if-nez v3, :cond_8

    goto :goto_5

    :cond_8
    invoke-virtual {v14}, Logo;->l()Landroid/content/Context;

    move-result-object v3

    invoke-virtual {v12}, Ljava/lang/Integer;->intValue()I

    move-result v12

    invoke-static {v3, v12}, Lrqi;->g(Landroid/content/Context;I)I

    move-result v3

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v12

    invoke-virtual {v12}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    invoke-virtual {v14, v3, v15}, Logo;->s(IZ)V

    goto :goto_4

    :cond_9
    instance-of v3, v12, Landroid/graphics/Bitmap;

    if-eqz v3, :cond_a

    check-cast v12, Landroid/graphics/Bitmap;

    invoke-virtual {v14}, Logo;->q()V

    iget-object v3, v14, Logo;->a:Landroid/view/View;

    check-cast v3, Landroid/widget/ImageView;

    invoke-virtual {v3, v12}, Landroid/widget/ImageView;->setImageBitmap(Landroid/graphics/Bitmap;)V

    goto :goto_4

    :cond_a
    instance-of v3, v12, Landroid/graphics/drawable/Drawable;

    if-eqz v3, :cond_b

    check-cast v12, Landroid/graphics/drawable/Drawable;

    invoke-virtual {v14, v12}, Logo;->r(Landroid/graphics/drawable/Drawable;)V

    goto :goto_4

    :cond_b
    instance-of v3, v12, Ldpv;

    if-eqz v3, :cond_c

    check-cast v12, Ldpv;

    invoke-virtual {v12, v14}, Ldpv;->q(Lebm;)V

    :goto_4
    invoke-virtual {v2}, Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;->c()I

    move-result v3

    invoke-virtual {v11, v3}, Landroid/widget/ImageView;->setImageAlpha(I)V

    invoke-virtual {v11, v5}, Landroid/widget/ImageView;->setVisibility(I)V

    invoke-virtual {v11, v10}, Landroid/widget/ImageView;->setImportantForAccessibility(I)V

    goto :goto_6

    :cond_c
    :goto_5
    invoke-virtual {v11, v4}, Landroid/widget/ImageView;->setVisibility(I)V

    :goto_6
    add-int/lit8 v9, v9, 0x1

    goto/16 :goto_3

    :cond_d
    iget-object v2, v0, Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;->e:Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;

    const v3, 0x7f0b0651

    if-eqz v1, :cond_12

    iget-object v1, v1, Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;->h:[I

    array-length v6, v1

    move v7, v5

    :goto_7
    if-ge v7, v6, :cond_12

    aget v8, v1, v7

    iget-object v9, v2, Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;->h:[I

    array-length v11, v9

    move v12, v5

    :goto_8
    if-ge v12, v11, :cond_f

    aget v13, v9, v12

    if-ne v13, v8, :cond_e

    goto :goto_9

    :cond_e
    add-int/lit8 v12, v12, 0x1

    goto :goto_8

    :cond_f
    if-nez v8, :cond_10

    move v8, v3

    :cond_10
    invoke-virtual {v0, v8}, Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;->findViewById(I)Landroid/view/View;

    move-result-object v8

    if-eqz v8, :cond_11

    invoke-virtual {v8, v4}, Landroid/view/View;->setVisibility(I)V

    :cond_11
    :goto_9
    add-int/lit8 v7, v7, 0x1

    goto :goto_7

    :cond_12
    iget-object v1, v2, Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;->g:[Ljava/lang/CharSequence;

    iget-object v2, v2, Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;->h:[I

    array-length v6, v1

    move v7, v5

    :goto_a
    if-ge v7, v6, :cond_18

    aget v8, v2, v7

    if-nez v8, :cond_13

    move v8, v3

    :cond_13
    invoke-virtual {v0, v8}, Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;->findViewById(I)Landroid/view/View;

    move-result-object v8

    aget-object v9, v1, v7

    if-nez v8, :cond_14

    goto :goto_d

    :cond_14
    invoke-static {v9}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v11

    if-nez v11, :cond_17

    instance-of v11, v8, Landroid/widget/TextView;

    if-eqz v11, :cond_15

    move-object v11, v8

    check-cast v11, Landroid/widget/TextView;

    invoke-virtual {v11, v9}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto :goto_b

    :cond_15
    instance-of v11, v8, Lcom/google/android/libraries/inputmethod/emoji/view/EmojiView;

    if-eqz v11, :cond_16

    move-object v11, v8

    check-cast v11, Lcom/google/android/libraries/inputmethod/emoji/view/EmojiView;

    invoke-static {}, Lntu;->a()Lntt;

    move-result-object v12

    invoke-interface {v9}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    move-result-object v9

    invoke-virtual {v12, v9}, Lntt;->d(Ljava/lang/String;)V

    invoke-virtual {v12}, Lntt;->a()Lntu;

    move-result-object v9

    invoke-virtual {v11, v9}, Lcom/google/android/libraries/inputmethod/emoji/view/EmojiView;->e(Lntu;)V

    :cond_16
    :goto_b
    invoke-virtual {v8, v5}, Landroid/view/View;->setVisibility(I)V

    goto :goto_c

    :cond_17
    invoke-virtual {v8, v4}, Landroid/view/View;->setVisibility(I)V

    :goto_c
    invoke-virtual {v8, v10}, Landroid/view/View;->setImportantForAccessibility(I)V

    :goto_d
    add-int/lit8 v7, v7, 0x1

    goto :goto_a

    :cond_18
    return-void
.end method
