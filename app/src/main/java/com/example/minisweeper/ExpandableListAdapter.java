package com.example.minisweeper;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> header_list;
    private Map<String, List<String>> children_map;

    public ExpandableListAdapter(Context context, List<String> header_list, Map<String, List<String>> children_map) {
        this.context = context;
        this.header_list = header_list;
        this.children_map = children_map;
    }

    @Override
    public String getChild(int groupPosition, int childPosititon) {
        return children_map.get(header_list.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_children, null);
        }
        TextView txtListChild = convertView.findViewById(R.id.children_title);
        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(children_map.containsKey(header_list.get(groupPosition))){
            return children_map.get(header_list.get(groupPosition)).size();
        }else{
            return 0;
        }
    }

    @Override
    public String getGroup(int groupPosition) {
        return header_list.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return header_list.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition);
        LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView lblListHeader = null;
        if (groupPosition != 1 && groupPosition != 2) {
            convertView = infalInflater.inflate(R.layout.list_header, null);
            lblListHeader = convertView.findViewById(R.id.header_title);
        }else{
            convertView = infalInflater.inflate(R.layout.switch_header, null);
            lblListHeader = convertView.findViewById(R.id.switch_header_title);
        }
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}