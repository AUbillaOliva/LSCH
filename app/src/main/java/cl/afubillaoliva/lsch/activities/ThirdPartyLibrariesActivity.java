package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.models.ListItem;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class ThirdPartyLibrariesActivity extends AppCompatActivity {

    private final Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final SharedPreference mSharedPreferences = new SharedPreference(context);
        if(mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.third_party_libraries_layout);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final TextView toolbarTitle = mToolbar.findViewById(R.id.toolbar_title);
        final RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        toolbarTitle.setText(R.string.third_party_libraries_title);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        final GenericAdapter<ListItem> adapter = new GenericAdapter<ListItem>(libraries()) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.third_party_libraries_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, ListItem val, int position) {
                final GenericViewHolder myViewHolder = (GenericViewHolder) holder;
                final TextView title = myViewHolder.get(R.id.library_title);
                title.setText(val.getTitle());
                final TextView subtitle = myViewHolder.get(R.id.library_license);
                subtitle.setText(val.getSubtitle());
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack() {
                return null;
            }
        };
        mRecyclerView.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private ArrayList<ListItem> libraries(){

        final ArrayList<ListItem> items = new ArrayList<>();

        ListItem item = new ListItem();
        item.setTitle("ACRA");
        item.setSubtitle("The following software may be included in this\n" +
                "product: ACRA. This software contains the following license and notice below:\n" +
                "\n" +
                "Application Crash Reporting for Android\n" +
                "Copyright 2010 Emmanuel Astier & Kevin Gaudin\n" +
                "\n" +
                "This product includes software developed at ACRA (http://acra.googlecode.com/).\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.");
        items.add(item);

        item = new ListItem();
        item.setTitle("Android Open Source Project");
        item.setSubtitle("The following software may be included in this product: Android Open Source Project. This software contains the following license and notice below:\n" +
                "\n" +
                "=========================================================================\n" +
                "NOTICE file corresponding to the section 4 d of the Apache License, Version 2.0, in this case for the Android-specific code.\n" +
                "=========================================================================\n" +
                "\n" +
                "Android Code\n" +
                "Copyright 2005-2008 The Android Open Source Project\n" +
                "\n" +
                "This product includes software developed as part of The Android Open Source Project (http://source.android.com).\n" +
                "\n" +
                "=========================================================================\n" +
                "NOTICE file corresponding to the section 4 d of the Apache License, Version 2.0, in this case for Apache Commons code.\n" +
                "=========================================================================\n" +
                "\n" +
                "Apache Commons\n" +
                "Copyright 1999-2004 The Apache Software Foundation\n" +
                "\n" +
                "This product includes software developed at\n" +
                "The Apache Software Foundation (http://www.apache.org/).\n" +
                "\n" +
                "=========================================================================\n" +
                "NOTICE file corresponding to the section 4 d of  == ==  the Apache License, Version 2.0, in this case for Jakarta Commons Logging.\n" +
                "=========================================================================\n" +
                "\n" +
                "Jakarta Commons Logging (JCL)\n" +
                "Copyright 2005,2006 The Apache Software Foundation.\n" +
                "\n" +
                "This product includes software developed at\n" +
                "The Apache Software Foundation (http://www.apache.org/).\n" +
                "\n" +
                "=========================================================================\n" +
                "NOTICE file corresponding to the section 4 d of  == ==  the Apache License, Version 2.0, in this case for the Nuance code.\n" +
                "=========================================================================\n" +
                "\n" +
                "These files are Copyright 2007 Nuance Communications, but released under the Apache2 License.\n" +
                "\n" +
                "=========================================================================\n" +
                "NOTICE file corresponding to the section 4 d of  == ==  the Apache License, Version 2.0, in this case for the Media Codecs code.\n" +
                "=========================================================================\n" +
                "\n" +
                "Media Codecs\n" +
                "These files are Copyright 1998 - 2009 PacketVideo, but released under the Apache2 License.\n" +
                "\n" +
                "=========================================================================\n" +
                "NOTICE file corresponding to the section 4 d of the Apache License, Version 2.0, in this case for Additional Codecs code.\n" +
                "=========================================================================\n" +
                "\n" +
                "Additional Codecs\n" +
                "These files are Copyright 2003-2010 VisualOn, but released under the Apache2 License.\n" +
                "\n" +
                "=========================================================================\n" +
                "NOTICE file corresponding to the section 4 d of the Apache License, Version 2.0, in this case for the Audio Effects code.\n" +
                "=========================================================================\n" +
                "\n" +
                "Audio Effects\n" +
                "These files are Copyright (C) 2004-2010 NXP Software and Copyright (C) 2010 The Android Open Source Project, but released under the Apache2 License.\n" +
                "\n" +
                " Apache License\n" +
                "Version 2.0, January 2004\n" +
                "http://www.apache.org/licenses/\n" +
                "\n" +
                "TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
                "\n" +
                "1. Definitions.\n" +
                "\n" +
                "\"License\" shall mean the terms and conditions for use, reproduction, and distribution as defined by Sections 1 through 9 of this document.\n" +
                "\n" +
                "\"Licensor\" shall mean the copyright owner or entity authorized by the copyright owner that is granting the License.\n" +
                "\n" +
                "\"Legal Entity\" shall mean the union of the acting entity and all other entities that control, are controlled by, or are under common control with that entity. For the purposes of this definition, \"control\" means (i) the power, direct or indirect, to cause the direction or management of such entity, whether by contract or otherwise, or (ii) ownership of fifty percent (50%) or more of the outstanding shares, or (iii) beneficial ownership of such entity.\n" +
                "\n" +
                "\"You\" (or \"Your\") shall mean an individual or Legal Entity exercising permissions granted by this License.\n" +
                "\n" +
                "\"Source\" form shall mean the preferred form for making modifications, including but not limited to software source code, documentation source, and configuration files.\n" +
                "\n" +
                "\"Object\" form shall mean any form resulting from mechanical transformation or translation of a Source form, including but not limited to compiled object code, generated documentation, and conversions to other media types.\n" +
                "\n" +
                "\"Work\" shall mean the work of authorship, whether in Source or Object form, made available under the License, as indicated by a copyright notice that is included in or attached to the work (an example is provided in the Appendix below).\n" +
                "\n" +
                "\"Derivative Works\" shall mean any work, whether in Source or Object form, that is based on (or derived from) the Work and for which the editorial revisions, annotations, elaborations, or other modifications represent, as a whole, an original work of authorship. For the purposes of this License, Derivative Works shall not include works that remain separable from, or merely link (or bind by name) to the interfaces of, the Work and Derivative Works thereof.\n" +
                "\n" +
                "\"Contribution\" shall mean any work of authorship, including the original version of the Work and any modifications or additions to that Work or Derivative Works thereof, that is intentionally submitted to Licensor for inclusion in the Work by the copyright owner or by an individual or Legal Entity authorized to submit on behalf of the copyright owner. For the purposes of this definition, \"submitted\" means any form of electronic, verbal, or written communication sent to the Licensor or its representatives, including but not limited to communication on electronic mailing lists, source code control systems, and issue tracking systems that are managed by, or on behalf of, the Licensor for the purpose of discussing and improving the Work, but excluding communication that is conspicuously marked or otherwise designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
                "\n" +
                "\"Contributor\" shall mean Licensor and any individual or Legal Entity on behalf of whom a Contribution has been received by Licensor and subsequently incorporated within the Work.\n" +
                "\n" +
                "2. Grant of Copyright License. Subject to the terms and conditions of this License, each Contributor hereby grants to You a perpetual, worldwide, non-exclusive, no-charge, royalty-free, irrevocable copyright license to reproduce, prepare Derivative Works of, publicly display, publicly perform, sublicense, and distribute the Work and such Derivative Works in Source or Object form.\n" +
                "\n" +
                "3. Grant of Patent License. Subject to the terms and conditions of this License, each Contributor hereby grants to You a perpetual, worldwide, non-exclusive, no-charge, royalty-free, irrevocable (except as stated in this section) patent license to make, have made, use, offer to sell, sell, import, and otherwise transfer the Work, where such license applies only to those patent claims licensable by such Contributor that are necessarily infringed by their Contribution(s) alone or by combination of their Contribution(s) with the Work to which such Contribution(s) was submitted. If You institute patent litigation against any entity (including a cross-claim or counterclaim in a lawsuit) alleging that the Work or a Contribution incorporated within the Work constitutes direct or contributory patent infringement, then any patent licenses granted to You under this License for that Work shall terminate as of the date such litigation is filed.\n" +
                "\n" +
                "4. Redistribution. You may reproduce and distribute copies of the Work or Derivative Works thereof in any medium, with or without modifications, and in Source or Object form, provided that You meet the following conditions:\n" +
                "\n" +
                "(a) You must give any other recipients of the Work or Derivative Works a copy of this License; and\n" +
                "\n" +
                "(b) You must cause any modified files to carry prominent notices stating that You changed the files; and\n" +
                "\n" +
                "(c) You must retain, in the Source form of any Derivative Works that You distribute, all copyright, patent, trademark, and attribution notices from the Source form of the Work, excluding those notices that do not pertain to any part of the Derivative Works; and\n" +
                "\n" +
                "(d) If the Work includes a \"NOTICE\" text file as part of its distribution, then any Derivative Works that You distribute must include a readable copy of the attribution notices contained within such NOTICE file, excluding those notices that do not pertain to any part of the Derivative Works, in at least one of the following places: within a NOTICE text file distributed as part of the Derivative Works; within the Source form or documentation, if provided along with the Derivative Works; or, within a display generated by the Derivative Works, if and wherever such third-party notices normally appear. The contents of the NOTICE file are for informational purposes only and do not modify the License. You may add Your own attribution notices within Derivative Works that You distribute, alongside or as an addendum to the NOTICE text from the Work, provided that such additional attribution notices cannot be construed as modifying the License.\n" +
                "\n" +
                "You may add Your own copyright statement to Your modifications and may provide additional or different license terms and conditions for use, reproduction, or distribution of Your modifications, or for any such Derivative Works as a whole, provided Your use, reproduction, and distribution of the Work otherwise complies with the conditions stated in this License.\n" +
                "\n" +
                "5. Submission of Contributions. Unless You explicitly state otherwise, any Contribution intentionally submitted for inclusion in the Work by You to the Licensor shall be under the terms and conditions of this License, without any additional terms or conditions. Notwithstanding the above, nothing herein shall supersede or modify the terms of any separate license agreement you may have executed with Licensor regarding such Contributions.\n" +
                "\n" +
                "6. Trademarks. This License does not grant permission to use the trade names, trademarks, service marks, or product names of the Licensor, except as required for reasonable and customary use in describing the origin of the Work and reproducing the content of the NOTICE file.\n" +
                "\n" +
                "7. Disclaimer of Warranty. Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE. You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.\n" +
                "\n" +
                "8. Limitation of Liability. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law (such as deliberate and grossly negligent acts) or agreed to in writing, shall any Contributor be liable to You for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work (including but not limited to damages for loss of goodwill, work stoppage, computer failure or malfunction, or any and all other commercial damages or losses), even if such Contributor has been advised of the possibility of such damages.\n" +
                "\n" +
                "9. Accepting Warranty or Additional Liability. While redistributing the Work or Derivative Works thereof, You may choose to offer, and charge a fee for, acceptance of support, warranty, indemnity, or other liability obligations and/or rights consistent with this License. However, in accepting such obligations, You may act only on Your own behalf and on Your sole responsibility, not on behalf of any other Contributor, and only if You agree to indemnify, defend, and hold each Contributor harmless for any liability incurred by, or claims asserted against, such Contributor by reason of your accepting any such warranty or additional liability.\n" +
                "\n" +
                "END OF TERMS AND CONDITIONS\n" +
                "\n" +
                "\n" +
                "UNICODE, INC. LICENSE AGREEMENT - DATA FILES AND SOFTWARE\n" +
                "\n" +
                "Unicode Data Files include all data files under the directories http://www.unicode.org/Public/, http://www.unicode.org/reports/, and http://www.unicode.org/cldr/data/ . Unicode Software includes any source code published in the Unicode Standard or under the directories http://www.unicode.org/Public/, http://www.unicode.org/reports/, and http://www.unicode.org/cldr/data/.\n" +
                "\n" +
                "NOTICE TO USER: Carefully read the following legal agreement. BY DOWNLOADING, INSTALLING, COPYING OR OTHERWISE USING UNICODE INC.'S DATA FILES (\"DATA FILES\"), AND/OR SOFTWARE (\"SOFTWARE\"), YOU UNEQUIVOCALLY ACCEPT, AND AGREE TO BE BOUND BY, ALL OF THE TERMS AND CONDITIONS OF THIS AGREEMENT. IF YOU DO NOT AGREE, DO NOT DOWNLOAD, INSTALL, COPY, DISTRIBUTE OR USE THE DATA FILES OR SOFTWARE.\n" +
                "\n" +
                "COPYRIGHT AND PERMISSION NOTICE\n" +
                "\n" +
                "Copyright (C) 1991-2008 Unicode, Inc. All rights reserved. Distributed under the Terms of Use in http://www.unicode.org/copyright.html.\n" +
                "\n" +
                "Permission is hereby granted, free of charge, to any person obtaining a copy of the Unicode data files and any associated documentation (the \"Data Files\") or Unicode software and any associated documentation (the \"Software\") to deal in the Data Files or Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, and/or sell copies of the Data Files or Software, and to permit persons to whom the Data Files or Software are furnished to do so, provided that (a) the above copyright notice(s) and this permission notice appear with all copies of the Data Files or Software, (b) both the above copyright notice(s) and this permission notice appear in associated documentation, and (c) there is clear notice in each modified Data File or in the Software as well as in the documentation associated with the Data File(s) or Software that the data or software has been modified.\n" +
                "\n" +
                "THE DATA FILES AND SOFTWARE ARE PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR HOLDERS INCLUDED IN THIS NOTICE BE LIABLE FOR ANY CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THE DATA FILES OR SOFTWARE.\n" +
                "\n" +
                "Except as contained in this notice, the name of a copyright holder shall not be used in advertising or otherwise to promote the sale, use or other dealings in these Data Files or Software without prior written authorization of the copyright holder.");
        items.add(item);

        item = new ListItem();
        item.setTitle("Android Material Components");
        item.setSubtitle("The following software may be included in this product: Android Material Components for Android. This software contains the following license and notice below:\n" +
        "\n" +
                "Apache License\n" +
                "Version 2.0, January 2004\n" +
                "http://www.apache.org/licenses/\n" +
                "\n" +
                "TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
                "\n" +
                "1. Definitions.\n" +
                "\n" +
                "\"License\" shall mean the terms and conditions for use, reproduction,\n" +
                "and distribution as defined by Sections 1 through 9 of this document.\n" +
                "\n" +
                "\"Licensor\" shall mean the copyright owner or entity authorized by\n" +
                "the copyright owner that is granting the License.\n" +
                "\n" +
                "\"Legal Entity\" shall mean the union of the acting entity and all\n" +
                "other entities that control, are controlled by, or are under common\n" +
                "control with that entity. For the purposes of this definition,\n" +
                "\"control\" means (i) the power, direct or indirect, to cause the\n" +
                "direction or management of such entity, whether by contract or\n" +
                "otherwise, or (ii) ownership of fifty percent (50%) or more of the\n" +
                "outstanding shares, or (iii) beneficial ownership of such entity.\n" +
                "\n" +
                "\"You\" (or \"Your\") shall mean an individual or Legal Entity\n" +
                "exercising permissions granted by this License.\n" +
                "\n" +
                "\"Source\" form shall mean the preferred form for making modifications,\n" +
                "including but not limited to software source code, documentation\n" +
                "source, and configuration files.\n" +
                "\n" +
                "\"Object\" form shall mean any form resulting from mechanical\n" +
                "transformation or translation of a Source form, including but\n" +
                "not limited to compiled object code, generated documentation,\n" +
                "and conversions to other media types.\n" +
                "\n" +
                "\"Work\" shall mean the work of authorship, whether in Source or\n" +
                "Object form, made available under the License, as indicated by a\n" +
                "copyright notice that is included in or attached to the work\n" +
                "(an example is provided in the Appendix below).\n" +
                "\n" +
                "\"Derivative Works\" shall mean any work, whether in Source or Object\n" +
                "form, that is based on (or derived from) the Work and for which the\n" +
                "editorial revisions, annotations, elaborations, or other modifications\n" +
                "represent, as a whole, an original work of authorship. For the purposes\n" +
                "of this License, Derivative Works shall not include works that remain\n" +
                "separable from, or merely link (or bind by name) to the interfaces of,\n" +
                "the Work and Derivative Works thereof.\n" +
                "\n" +
                "\"Contribution\" shall mean any work of authorship, including\n" +
                "the original version of the Work and any modifications or additions\n" +
                "to that Work or Derivative Works thereof, that is intentionally\n" +
                "submitted to Licensor for inclusion in the Work by the copyright owner\n" +
                "or by an individual or Legal Entity authorized to submit on behalf of\n" +
                "the copyright owner. For the purposes of this definition, \"submitted\"\n" +
                "means any form of electronic, verbal, or written communication sent\n" +
                "to the Licensor or its representatives, including but not limited to\n" +
                "communication on electronic mailing lists, source code control systems,\n" +
                "and issue tracking systems that are managed by, or on behalf of, the\n" +
                "Licensor for the purpose of discussing and improving the Work, but\n" +
                "excluding communication that is conspicuously marked or otherwise\n" +
                "designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
                "\n" +
                "\"Contributor\" shall mean Licensor and any individual or Legal Entity\n" +
                "on behalf of whom a Contribution has been received by Licensor and\n" +
                "subsequently incorporated within the Work.\n" +
                "\n" +
                "2. Grant of Copyright License. Subject to the terms and conditions of\n" +
                "this License, each Contributor hereby grants to You a perpetual,\n" +
                "worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
                "copyright license to reproduce, prepare Derivative Works of,\n" +
                "publicly display, publicly perform, sublicense, and distribute the\n" +
                "Work and such Derivative Works in Source or Object form.\n" +
                "\n" +
                "3. Grant of Patent License. Subject to the terms and conditions of\n" +
                "this License, each Contributor hereby grants to You a perpetual,\n" +
                "worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
                "(except as stated in this section) patent license to make, have made,\n" +
                "use, offer to sell, sell, import, and otherwise transfer the Work,\n" +
                "where such license applies only to those patent claims licensable\n" +
                "by such Contributor that are necessarily infringed by their\n" +
                "Contribution(s) alone or by combination of their Contribution(s)\n" +
                "with the Work to which such Contribution(s) was submitted. If You\n" +
                "institute patent litigation against any entity (including a\n" +
                "cross-claim or counterclaim in a lawsuit) alleging that the Work\n" +
                "or a Contribution incorporated within the Work constitutes direct\n" +
                "or contributory patent infringement, then any patent licenses\n" +
                "granted to You under this License for that Work shall terminate\n" +
                "as of the date such litigation is filed.\n" +
                "\n" +
                "4. Redistribution. You may reproduce and distribute copies of the\n" +
                "Work or Derivative Works thereof in any medium, with or without\n" +
                "modifications, and in Source or Object form, provided that You\n" +
                "meet the following conditions:\n" +
                "\n" +
                "(a) You must give any other recipients of the Work or\n" +
                "Derivative Works a copy of this License; and\n" +
                "\n" +
                "(b) You must cause any modified files to carry prominent notices\n" +
                "stating that You changed the files; and\n" +
                "\n" +
                "(c) You must retain, in the Source form of any Derivative Works\n" +
                "that You distribute, all copyright, patent, trademark, and\n" +
                "attribution notices from the Source form of the Work,\n" +
                "excluding those notices that do not pertain to any part of\n" +
                "the Derivative Works; and\n" +
                "\n" +
                "(d) If the Work includes a \"NOTICE\" text file as part of its\n" +
                "distribution, then any Derivative Works that You distribute must\n" +
                "include a readable copy of the attribution notices contained\n" +
                "within such NOTICE file, excluding those notices that do not\n" +
                "pertain to any part of the Derivative Works, in at least one\n" +
                "of the following places: within a NOTICE text file distributed\n" +
                "as part of the Derivative Works; within the Source form or\n" +
                "documentation, if provided along with the Derivative Works; or,\n" +
                "within a display generated by the Derivative Works, if and\n" +
                "wherever such third-party notices normally appear. The contents\n" +
                "of the NOTICE file are for informational purposes only and\n" +
                "do not modify the License. You may add Your own attribution\n" +
                "notices within Derivative Works that You distribute, alongside\n" +
                "or as an addendum to the NOTICE text from the Work, provided\n" +
                "that such additional attribution notices cannot be construed\n" +
                "as modifying the License.\n" +
                "\n" +
                "You may add Your own copyright statement to Your modifications and\n" +
                "may provide additional or different license terms and conditions\n" +
                "for use, reproduction, or distribution of Your modifications, or\n" +
                "for any such Derivative Works as a whole, provided Your use,\n" +
                "reproduction, and distribution of the Work otherwise complies with\n" +
                "the conditions stated in this License.\n" +
                "\n" +
                "5. Submission of Contributions. Unless You explicitly state otherwise,\n" +
                "any Contribution intentionally submitted for inclusion in the Work\n" +
                "by You to the Licensor shall be under the terms and conditions of\n" +
                "this License, without any additional terms or conditions.\n" +
                "Notwithstanding the above, nothing herein shall supersede or modify\n" +
                "the terms of any separate license agreement you may have executed\n" +
                "with Licensor regarding such Contributions.\n" +
                "\n" +
                "6. Trademarks. This License does not grant permission to use the trade\n" +
                "names, trademarks, service marks, or product names of the Licensor,\n" +
                "except as required for reasonable and customary use in describing the\n" +
                "origin of the Work and reproducing the content of the NOTICE file.\n" +
                "\n" +
                "7. Disclaimer of Warranty. Unless required by applicable law or\n" +
                "agreed to in writing, Licensor provides the Work (and each\n" +
                "Contributor provides its Contributions) on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n" +
                "implied, including, without limitation, any warranties or conditions\n" +
                "of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n" +
                "PARTICULAR PURPOSE. You are solely responsible for determining the\n" +
                "appropriateness of using or redistributing the Work and assume any\n" +
                "risks associated with Your exercise of permissions under this License.\n" +
                "\n" +
                "8. Limitation of Liability. In no event and under no legal theory,\n" +
                "whether in tort (including negligence), contract, or otherwise,\n" +
                "unless required by applicable law (such as deliberate and grossly\n" +
                "negligent acts) or agreed to in writing, shall any Contributor be\n" +
                "liable to You for damages, including any direct, indirect, special,\n" +
                "incidental, or consequential damages of any character arising as a\n" +
                "result of this License or out of the use or inability to use the\n" +
                "Work (including but not limited to damages for loss of goodwill,\n" +
                "work stoppage, computer failure or malfunction, or any and all\n" +
                "other commercial damages or losses), even if such Contributor\n" +
                "has been advised of the possibility of such damages.\n" +
                "\n" +
                "9. Accepting Warranty or Additional Liability. While redistributing\n" +
                "the Work or Derivative Works thereof, You may choose to offer,\n" +
                "and charge a fee for, acceptance of support, warranty, indemnity,\n" +
                "or other liability obligations and/or rights consistent with this\n" +
                "License. However, in accepting such obligations, You may act only\n" +
                "on Your own behalf and on Your sole responsibility, not on behalf\n" +
                "of any other Contributor, and only if You agree to indemnify,\n" +
                "defend, and hold each Contributor harmless for any liability\n" +
                "incurred by, or claims asserted against, such Contributor by reason\n" +
                "of your accepting any such warranty or additional liability.\n" +
                "\n" +
                "END OF TERMS AND CONDITIONS\n" +
                "\n" +
                "APPENDIX: How to apply the Apache License to your work.\n" +
                "\n" +
                "To apply the Apache License to your work, attach the following\n" +
                "boilerplate notice, with the fields enclosed by brackets \"[]\"\n" +
                "replaced with your own identifying information. (Don't include\n" +
                "the brackets!)  The text should be enclosed in the appropriate\n" +
                "comment syntax for the file format. We also recommend that a\n" +
                "file or class name and description of purpose be included on the\n" +
                "same \"printed page\" as the copyright notice for easier\n" +
                "identification within third-party archives.\n" +
                "\n" +
                "Copyright [yyyy] [name of copyright owner]\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.\n");
        items.add(item);

        item = new ListItem();
        item.setTitle("Android-CardView");
        item.setSubtitle("The following software may be included in this product: Android CardView. This software contains the following license and notice below:\n"+
                "Apache License\n" +
                "--------------\n" +
                "\n" +
                "            Version 2.0, January 2004\n" +
                "        http://www.apache.org/licenses/\n" +
                "\n" +
                "TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
                "\n" +
                "1. Definitions.\n" +
                "\n" +
                "\"License\" shall mean the terms and conditions for use, reproduction,\n" +
                "and distribution as defined by Sections 1 through 9 of this document.\n" +
                "\n" +
                "\"Licensor\" shall mean the copyright owner or entity authorized by\n" +
                "the copyright owner that is granting the License.\n" +
                "\n" +
                "\"Legal Entity\" shall mean the union of the acting entity and all\n" +
                "other entities that control, are controlled by, or are under common\n" +
                "control with that entity. For the purposes of this definition,\n" +
                "\"control\" means (i) the power, direct or indirect, to cause the\n" +
                "direction or management of such entity, whether by contract or\n" +
                "otherwise, or (ii) ownership of fifty percent (50%) or more of the\n" +
                "outstanding shares, or (iii) beneficial ownership of such entity.\n" +
                "\n" +
                "\"You\" (or \"Your\") shall mean an individual or Legal Entity\n" +
                "exercising permissions granted by this License.\n" +
                "\n" +
                "\"Source\" form shall mean the preferred form for making modifications,\n" +
                "including but not limited to software source code, documentation\n" +
                "source, and configuration files.\n" +
                "\n" +
                "\"Object\" form shall mean any form resulting from mechanical\n" +
                "transformation or translation of a Source form, including but\n" +
                "not limited to compiled object code, generated documentation,\n" +
                "and conversions to other media types.\n" +
                "\n" +
                "\"Work\" shall mean the work of authorship, whether in Source or\n" +
                "Object form, made available under the License, as indicated by a\n" +
                "copyright notice that is included in or attached to the work\n" +
                "(an example is provided in the Appendix below).\n" +
                "\n" +
                "\"Derivative Works\" shall mean any work, whether in Source or Object\n" +
                "form, that is based on (or derived from) the Work and for which the\n" +
                "editorial revisions, annotations, elaborations, or other modifications\n" +
                "represent, as a whole, an original work of authorship. For the purposes\n" +
                "of this License, Derivative Works shall not include works that remain\n" +
                "separable from, or merely link (or bind by name) to the interfaces of,\n" +
                "the Work and Derivative Works thereof.\n" +
                "\n" +
                "\"Contribution\" shall mean any work of authorship, including\n" +
                "the original version of the Work and any modifications or additions\n" +
                "to that Work or Derivative Works thereof, that is intentionally\n" +
                "submitted to Licensor for inclusion in the Work by the copyright owner\n" +
                "or by an individual or Legal Entity authorized to submit on behalf of\n" +
                "the copyright owner. For the purposes of this definition, \"submitted\"\n" +
                "means any form of electronic, verbal, or written communication sent\n" +
                "to the Licensor or its representatives, including but not limited to\n" +
                "communication on electronic mailing lists, source code control systems,\n" +
                "and issue tracking systems that are managed by, or on behalf of, the\n" +
                "Licensor for the purpose of discussing and improving the Work, but\n" +
                "excluding communication that is conspicuously marked or otherwise\n" +
                "designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
                "\n" +
                "\"Contributor\" shall mean Licensor and any individual or Legal Entity\n" +
                "on behalf of whom a Contribution has been received by Licensor and\n" +
                "subsequently incorporated within the Work.\n" +
                "\n" +
                "2. Grant of Copyright License. Subject to the terms and conditions of\n" +
                "this License, each Contributor hereby grants to You a perpetual,\n" +
                "worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
                "copyright license to reproduce, prepare Derivative Works of,\n" +
                "publicly display, publicly perform, sublicense, and distribute the\n" +
                "Work and such Derivative Works in Source or Object form.\n" +
                "\n" +
                "3. Grant of Patent License. Subject to the terms and conditions of\n" +
                "this License, each Contributor hereby grants to You a perpetual,\n" +
                "worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
                "(except as stated in this section) patent license to make, have made,\n" +
                "use, offer to sell, sell, import, and otherwise transfer the Work,\n" +
                "where such license applies only to those patent claims licensable\n" +
                "by such Contributor that are necessarily infringed by their\n" +
                "Contribution(s) alone or by combination of their Contribution(s)\n" +
                "with the Work to which such Contribution(s) was submitted. If You\n" +
                "institute patent litigation against any entity (including a\n" +
                "cross-claim or counterclaim in a lawsuit) alleging that the Work\n" +
                "or a Contribution incorporated within the Work constitutes direct\n" +
                "or contributory patent infringement, then any patent licenses\n" +
                "granted to You under this License for that Work shall terminate\n" +
                "as of the date such litigation is filed.\n" +
                "\n" +
                "4. Redistribution. You may reproduce and distribute copies of the\n" +
                "Work or Derivative Works thereof in any medium, with or without\n" +
                "modifications, and in Source or Object form, provided that You\n" +
                "meet the following conditions:\n" +
                "\n" +
                "(a) You must give any other recipients of the Work or\n" +
                "Derivative Works a copy of this License; and\n" +
                "\n" +
                "(b) You must cause any modified files to carry prominent notices\n" +
                "stating that You changed the files; and\n" +
                "\n" +
                "(c) You must retain, in the Source form of any Derivative Works\n" +
                "that You distribute, all copyright, patent, trademark, and\n" +
                "attribution notices from the Source form of the Work,\n" +
                "excluding those notices that do not pertain to any part of\n" +
                "the Derivative Works; and\n" +
                "\n" +
                "(d) If the Work includes a \"NOTICE\" text file as part of its\n" +
                "distribution, then any Derivative Works that You distribute must\n" +
                "include a readable copy of the attribution notices contained\n" +
                "within such NOTICE file, excluding those notices that do not\n" +
                "pertain to any part of the Derivative Works, in at least one\n" +
                "of the following places: within a NOTICE text file distributed\n" +
                "as part of the Derivative Works; within the Source form or\n" +
                "documentation, if provided along with the Derivative Works; or,\n" +
                "within a display generated by the Derivative Works, if and\n" +
                "wherever such third-party notices normally appear. The contents\n" +
                "of the NOTICE file are for informational purposes only and\n" +
                "do not modify the License. You may add Your own attribution\n" +
                "notices within Derivative Works that You distribute, alongside\n" +
                "or as an addendum to the NOTICE text from the Work, provided\n" +
                "that such additional attribution notices cannot be construed\n" +
                "as modifying the License.\n" +
                "\n" +
                "You may add Your own copyright statement to Your modifications and\n" +
                "may provide additional or different license terms and conditions\n" +
                "for use, reproduction, or distribution of Your modifications, or\n" +
                "for any such Derivative Works as a whole, provided Your use,\n" +
                "reproduction, and distribution of the Work otherwise complies with\n" +
                "the conditions stated in this License.\n" +
                "\n" +
                "5. Submission of Contributions. Unless You explicitly state otherwise,\n" +
                "any Contribution intentionally submitted for inclusion in the Work\n" +
                "by You to the Licensor shall be under the terms and conditions of\n" +
                "this License, without any additional terms or conditions.\n" +
                "Notwithstanding the above, nothing herein shall supersede or modify\n" +
                "the terms of any separate license agreement you may have executed\n" +
                "with Licensor regarding such Contributions.\n" +
                "\n" +
                "6. Trademarks. This License does not grant permission to use the trade\n" +
                "names, trademarks, service marks, or product names of the Licensor,\n" +
                "except as required for reasonable and customary use in describing the\n" +
                "origin of the Work and reproducing the content of the NOTICE file.\n" +
                "\n" +
                "7. Disclaimer of Warranty. Unless required by applicable law or\n" +
                "agreed to in writing, Licensor provides the Work (and each\n" +
                "Contributor provides its Contributions) on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n" +
                "implied, including, without limitation, any warranties or conditions\n" +
                "of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n" +
                "PARTICULAR PURPOSE. You are solely responsible for determining the\n" +
                "appropriateness of using or redistributing the Work and assume any\n" +
                "risks associated with Your exercise of permissions under this License.\n" +
                "\n" +
                "8. Limitation of Liability. In no event and under no legal theory,\n" +
                "whether in tort (including negligence), contract, or otherwise,\n" +
                "unless required by applicable law (such as deliberate and grossly\n" +
                "negligent acts) or agreed to in writing, shall any Contributor be\n" +
                "liable to You for damages, including any direct, indirect, special,\n" +
                "incidental, or consequential damages of any character arising as a\n" +
                "result of this License or out of the use or inability to use the\n" +
                "Work (including but not limited to damages for loss of goodwill,\n" +
                "work stoppage, computer failure or malfunction, or any and all\n" +
                "other commercial damages or losses), even if such Contributor\n" +
                "has been advised of the possibility of such damages.\n" +
                "\n" +
                "9. Accepting Warranty or Additional Liability. While redistributing\n" +
                "the Work or Derivative Works thereof, You may choose to offer,\n" +
                "and charge a fee for, acceptance of support, warranty, indemnity,\n" +
                "or other liability obligations and/or rights consistent with this\n" +
                "License. However, in accepting such obligations, You may act only\n" +
                "on Your own behalf and on Your sole responsibility, not on behalf\n" +
                "of any other Contributor, and only if You agree to indemnify,\n" +
                "defend, and hold each Contributor harmless for any liability\n" +
                "incurred by, or claims asserted against, such Contributor by reason\n" +
                "of your accepting any such warranty or additional liability.\n" +
                "\n" +
                "END OF TERMS AND CONDITIONS\n" +
                "\n" +
                "APPENDIX: How to apply the Apache License to your work.\n" +
                "\n" +
                "To apply the Apache License to your work, attach the following\n" +
                "boilerplate notice, with the fields enclosed by brackets \"{}\"\n" +
                "replaced with your own identifying information. (Don't include\n" +
                "the brackets!)  The text should be enclosed in the appropriate\n" +
                "comment syntax for the file format. We also recommend that a\n" +
                "file or class name and description of purpose be included on the\n" +
                "same \"printed page\" as the copyright notice for easier\n" +
                "identification within third-party archives.\n" +
                "\n" +
                "Copyright {yyyy} {name of copyright owner}\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.");
        items.add(item);

        item = new ListItem();
        item.setTitle("Android-RecyclerView");
        item.setSubtitle("The following software may be included in this product: Android RecyclerView. This software contains the following license and notice below:\n"+
                "Apache License\n" +
                "--------------\n" +
                "\n" +
                "            Version 2.0, January 2004\n" +
                "        http://www.apache.org/licenses/\n" +
                "\n" +
                "TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
                "\n" +
                "1. Definitions.\n" +
                "\n" +
                "\"License\" shall mean the terms and conditions for use, reproduction,\n" +
                "and distribution as defined by Sections 1 through 9 of this document.\n" +
                "\n" +
                "\"Licensor\" shall mean the copyright owner or entity authorized by\n" +
                "the copyright owner that is granting the License.\n" +
                "\n" +
                "\"Legal Entity\" shall mean the union of the acting entity and all\n" +
                "other entities that control, are controlled by, or are under common\n" +
                "control with that entity. For the purposes of this definition,\n" +
                "\"control\" means (i) the power, direct or indirect, to cause the\n" +
                "direction or management of such entity, whether by contract or\n" +
                "otherwise, or (ii) ownership of fifty percent (50%) or more of the\n" +
                "outstanding shares, or (iii) beneficial ownership of such entity.\n" +
                "\n" +
                "\"You\" (or \"Your\") shall mean an individual or Legal Entity\n" +
                "exercising permissions granted by this License.\n" +
                "\n" +
                "\"Source\" form shall mean the preferred form for making modifications,\n" +
                "including but not limited to software source code, documentation\n" +
                "source, and configuration files.\n" +
                "\n" +
                "\"Object\" form shall mean any form resulting from mechanical\n" +
                "transformation or translation of a Source form, including but\n" +
                "not limited to compiled object code, generated documentation,\n" +
                "and conversions to other media types.\n" +
                "\n" +
                "\"Work\" shall mean the work of authorship, whether in Source or\n" +
                "Object form, made available under the License, as indicated by a\n" +
                "copyright notice that is included in or attached to the work\n" +
                "(an example is provided in the Appendix below).\n" +
                "\n" +
                "\"Derivative Works\" shall mean any work, whether in Source or Object\n" +
                "form, that is based on (or derived from) the Work and for which the\n" +
                "editorial revisions, annotations, elaborations, or other modifications\n" +
                "represent, as a whole, an original work of authorship. For the purposes\n" +
                "of this License, Derivative Works shall not include works that remain\n" +
                "separable from, or merely link (or bind by name) to the interfaces of,\n" +
                "the Work and Derivative Works thereof.\n" +
                "\n" +
                "\"Contribution\" shall mean any work of authorship, including\n" +
                "the original version of the Work and any modifications or additions\n" +
                "to that Work or Derivative Works thereof, that is intentionally\n" +
                "submitted to Licensor for inclusion in the Work by the copyright owner\n" +
                "or by an individual or Legal Entity authorized to submit on behalf of\n" +
                "the copyright owner. For the purposes of this definition, \"submitted\"\n" +
                "means any form of electronic, verbal, or written communication sent\n" +
                "to the Licensor or its representatives, including but not limited to\n" +
                "communication on electronic mailing lists, source code control systems,\n" +
                "and issue tracking systems that are managed by, or on behalf of, the\n" +
                "Licensor for the purpose of discussing and improving the Work, but\n" +
                "excluding communication that is conspicuously marked or otherwise\n" +
                "designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
                "\n" +
                "\"Contributor\" shall mean Licensor and any individual or Legal Entity\n" +
                "on behalf of whom a Contribution has been received by Licensor and\n" +
                "subsequently incorporated within the Work.\n" +
                "\n" +
                "2. Grant of Copyright License. Subject to the terms and conditions of\n" +
                "this License, each Contributor hereby grants to You a perpetual,\n" +
                "worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
                "copyright license to reproduce, prepare Derivative Works of,\n" +
                "publicly display, publicly perform, sublicense, and distribute the\n" +
                "Work and such Derivative Works in Source or Object form.\n" +
                "\n" +
                "3. Grant of Patent License. Subject to the terms and conditions of\n" +
                "this License, each Contributor hereby grants to You a perpetual,\n" +
                "worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
                "(except as stated in this section) patent license to make, have made,\n" +
                "use, offer to sell, sell, import, and otherwise transfer the Work,\n" +
                "where such license applies only to those patent claims licensable\n" +
                "by such Contributor that are necessarily infringed by their\n" +
                "Contribution(s) alone or by combination of their Contribution(s)\n" +
                "with the Work to which such Contribution(s) was submitted. If You\n" +
                "institute patent litigation against any entity (including a\n" +
                "cross-claim or counterclaim in a lawsuit) alleging that the Work\n" +
                "or a Contribution incorporated within the Work constitutes direct\n" +
                "or contributory patent infringement, then any patent licenses\n" +
                "granted to You under this License for that Work shall terminate\n" +
                "as of the date such litigation is filed.\n" +
                "\n" +
                "4. Redistribution. You may reproduce and distribute copies of the\n" +
                "Work or Derivative Works thereof in any medium, with or without\n" +
                "modifications, and in Source or Object form, provided that You\n" +
                "meet the following conditions:\n" +
                "\n" +
                "(a) You must give any other recipients of the Work or\n" +
                "Derivative Works a copy of this License; and\n" +
                "\n" +
                "(b) You must cause any modified files to carry prominent notices\n" +
                "stating that You changed the files; and\n" +
                "\n" +
                "(c) You must retain, in the Source form of any Derivative Works\n" +
                "that You distribute, all copyright, patent, trademark, and\n" +
                "attribution notices from the Source form of the Work,\n" +
                "excluding those notices that do not pertain to any part of\n" +
                "the Derivative Works; and\n" +
                "\n" +
                "(d) If the Work includes a \"NOTICE\" text file as part of its\n" +
                "distribution, then any Derivative Works that You distribute must\n" +
                "include a readable copy of the attribution notices contained\n" +
                "within such NOTICE file, excluding those notices that do not\n" +
                "pertain to any part of the Derivative Works, in at least one\n" +
                "of the following places: within a NOTICE text file distributed\n" +
                "as part of the Derivative Works; within the Source form or\n" +
                "documentation, if provided along with the Derivative Works; or,\n" +
                "within a display generated by the Derivative Works, if and\n" +
                "wherever such third-party notices normally appear. The contents\n" +
                "of the NOTICE file are for informational purposes only and\n" +
                "do not modify the License. You may add Your own attribution\n" +
                "notices within Derivative Works that You distribute, alongside\n" +
                "or as an addendum to the NOTICE text from the Work, provided\n" +
                "that such additional attribution notices cannot be construed\n" +
                "as modifying the License.\n" +
                "\n" +
                "You may add Your own copyright statement to Your modifications and\n" +
                "may provide additional or different license terms and conditions\n" +
                "for use, reproduction, or distribution of Your modifications, or\n" +
                "for any such Derivative Works as a whole, provided Your use,\n" +
                "reproduction, and distribution of the Work otherwise complies with\n" +
                "the conditions stated in this License.\n" +
                "\n" +
                "5. Submission of Contributions. Unless You explicitly state otherwise,\n" +
                "any Contribution intentionally submitted for inclusion in the Work\n" +
                "by You to the Licensor shall be under the terms and conditions of\n" +
                "this License, without any additional terms or conditions.\n" +
                "Notwithstanding the above, nothing herein shall supersede or modify\n" +
                "the terms of any separate license agreement you may have executed\n" +
                "with Licensor regarding such Contributions.\n" +
                "\n" +
                "6. Trademarks. This License does not grant permission to use the trade\n" +
                "names, trademarks, service marks, or product names of the Licensor,\n" +
                "except as required for reasonable and customary use in describing the\n" +
                "origin of the Work and reproducing the content of the NOTICE file.\n" +
                "\n" +
                "7. Disclaimer of Warranty. Unless required by applicable law or\n" +
                "agreed to in writing, Licensor provides the Work (and each\n" +
                "Contributor provides its Contributions) on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n" +
                "implied, including, without limitation, any warranties or conditions\n" +
                "of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n" +
                "PARTICULAR PURPOSE. You are solely responsible for determining the\n" +
                "appropriateness of using or redistributing the Work and assume any\n" +
                "risks associated with Your exercise of permissions under this License.\n" +
                "\n" +
                "8. Limitation of Liability. In no event and under no legal theory,\n" +
                "whether in tort (including negligence), contract, or otherwise,\n" +
                "unless required by applicable law (such as deliberate and grossly\n" +
                "negligent acts) or agreed to in writing, shall any Contributor be\n" +
                "liable to You for damages, including any direct, indirect, special,\n" +
                "incidental, or consequential damages of any character arising as a\n" +
                "result of this License or out of the use or inability to use the\n" +
                "Work (including but not limited to damages for loss of goodwill,\n" +
                "work stoppage, computer failure or malfunction, or any and all\n" +
                "other commercial damages or losses), even if such Contributor\n" +
                "has been advised of the possibility of such damages.\n" +
                "\n" +
                "9. Accepting Warranty or Additional Liability. While redistributing\n" +
                "the Work or Derivative Works thereof, You may choose to offer,\n" +
                "and charge a fee for, acceptance of support, warranty, indemnity,\n" +
                "or other liability obligations and/or rights consistent with this\n" +
                "License. However, in accepting such obligations, You may act only\n" +
                "on Your own behalf and on Your sole responsibility, not on behalf\n" +
                "of any other Contributor, and only if You agree to indemnify,\n" +
                "defend, and hold each Contributor harmless for any liability\n" +
                "incurred by, or claims asserted against, such Contributor by reason\n" +
                "of your accepting any such warranty or additional liability.\n" +
                "\n" +
                "END OF TERMS AND CONDITIONS\n" +
                "\n" +
                "APPENDIX: How to apply the Apache License to your work.\n" +
                "\n" +
                "To apply the Apache License to your work, attach the following\n" +
                "boilerplate notice, with the fields enclosed by brackets \"{}\"\n" +
                "replaced with your own identifying information. (Don't include\n" +
                "the brackets!)  The text should be enclosed in the appropriate\n" +
                "comment syntax for the file format. We also recommend that a\n" +
                "file or class name and description of purpose be included on the\n" +
                "same \"printed page\" as the copyright notice for easier\n" +
                "identification within third-party archives.\n" +
                "\n" +
                "Copyright {yyyy} {name of copyright owner}\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.");
        items.add(item);

        item = new ListItem();
        item.setTitle("Retrofit");
        item.setSubtitle("The following software may be included in this product: Retrofit. This software contains the following license below:\n"+
                "Copyright 2013 Square, Inc.\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.");
        items.add(item);

        item = new ListItem();
        item.setTitle("OkHttp3");
        item.setSubtitle("The following software may be included in this product: OkHttp. This software contains the following license below:\n" +
                "Copyright 2019 Square, Inc.\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.");
        items.add(item);

        item = new ListItem();
        item.setTitle("Glide");
        item.setSubtitle("The following software may be included in this product: Glide. This software contains the following license below:\n" +
                "License for everything not in third_party and not otherwise marked:\n" +
                "\n" +
                "Copyright 2014 Google, Inc. All rights reserved.\n" +
                "\n" +
                "Redistribution and use in source and binary forms, with or without modification, are\n" +
                "permitted provided that the following conditions are met:\n" +
                "\n" +
                "   1. Redistributions of source code must retain the above copyright notice, this list of\n" +
                "         conditions and the following disclaimer.\n" +
                "\n" +
                "   2. Redistributions in binary form must reproduce the above copyright notice, this list\n" +
                "         of conditions and the following disclaimer in the documentation and/or other materials\n" +
                "         provided with the distribution.\n" +
                "\n" +
                "THIS SOFTWARE IS PROVIDED BY GOOGLE, INC. ``AS IS'' AND ANY EXPRESS OR IMPLIED\n" +
                "WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND\n" +
                "FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GOOGLE, INC. OR\n" +
                "CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR\n" +
                "CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR\n" +
                "SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON\n" +
                "ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING\n" +
                "NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF\n" +
                "ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n" +
                "\n" +
                "The views and conclusions contained in the software and documentation are those of the\n" +
                "authors and should not be interpreted as representing official policies, either expressed\n" +
                "or implied, of Google, Inc.\n" +
                "---------------------------------------------------------------------------------------------\n" +
                "License for third_party/disklrucache:\n" +
                "\n" +
                "Copyright 2012 Jake Wharton\n" +
                "Copyright 2011 The Android Open Source Project\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.\n" +
                "---------------------------------------------------------------------------------------------\n" +
                "License for third_party/gif_decoder:\n" +
                "\n" +
                "Copyright (c) 2013 Xcellent Creations, Inc.\n" +
                "\n" +
                "Permission is hereby granted, free of charge, to any person obtaining\n" +
                "a copy of this software and associated documentation files (the\n" +
                "\"Software\"), to deal in the Software without restriction, including\n" +
                "without limitation the rights to use, copy, modify, merge, publish,\n" +
                "distribute, sublicense, and/or sell copies of the Software, and to\n" +
                "permit persons to whom the Software is furnished to do so, subject to\n" +
                "the following conditions:\n" +
                "\n" +
                "The above copyright notice and this permission notice shall be\n" +
                "included in all copies or substantial portions of the Software.\n" +
                "\n" +
                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND,\n" +
                "EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF\n" +
                "MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND\n" +
                "NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE\n" +
                "LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION\n" +
                "OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION\n" +
                "WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.\n" +
                "---------------------------------------------------------------------------------------------\n" +
                "License for third_party/gif_encoder/AnimatedGifEncoder.java and\n" +
                "third_party/gif_encoder/LZWEncoder.java:\n" +
                "\n" +
                "No copyright asserted on the source code of this class. May be used for any\n" +
                "purpose, however, refer to the Unisys LZW patent for restrictions on use of\n" +
                "the associated LZWEncoder class. Please forward any corrections to\n" +
                "kweiner@fmsware.com.\n" +
                "\n" +
                "-----------------------------------------------------------------------------\n" +
                "License for third_party/gif_encoder/NeuQuant.java\n" +
                "\n" +
                "Copyright (c) 1994 Anthony Dekker\n" +
                "\n" +
                "NEUQUANT Neural-Net quantization algorithm by Anthony Dekker, 1994. See\n" +
                "\"Kohonen neural networks for optimal colour quantization\" in \"Network:\n" +
                "Computation in Neural Systems\" Vol. 5 (1994) pp 351-367. for a discussion of\n" +
                "the algorithm.\n" +
                "\n" +
                "Any party obtaining a copy of these files from the author, directly or\n" +
                "indirectly, is granted, free of charge, a full and unrestricted irrevocable,\n" +
                "world-wide, paid up, royalty-free, nonexclusive right and license to deal in\n" +
                "this software and documentation files (the \"Software\"), including without\n" +
                "limitation the rights to use, copy, modify, merge, publish, distribute,\n" +
                "sublicense, and/or sell copies of the Software, and to permit persons who\n" +
                "receive copies from any such party to do so, with the only requirement being\n" +
                "that this copyright notice remain intact.");
        items.add(item);

        item = new ListItem();
        item.setTitle("Gson");
        item.setSubtitle("The following software may be included in this product: Gson. This software contains the following license below:\n" +
                "Copyright 2008 Google Inc.\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.");
        items.add(item);

        return items;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home){
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

}
