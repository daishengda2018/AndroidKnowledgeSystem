package com.example.dsd.demo;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private RecyclerViewAdapter mAdapter;

    private final int GROUP = 0xf01;
    private final int CHILD = 0xf02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        ArrayList<Item> mItems = new ArrayList<>();
        mAdapter = new RecyclerViewAdapter(mItems);
        mRecyclerView.setAdapter(mAdapter);

        String[] groups = {"A", "B", "C", "D", "E", "F"};

        for (int i = 0; i < groups.length; i++) {
            GroupItem groupItem = new GroupItem();
            groupItem.id = i;
            groupItem.title = groups[i];

            int count = (int) (Math.random() * 10) % 5 + 1;
            for (int j = 0; j < count; j++) {
                ChildItem childItem = new ChildItem();
                childItem.id = j;
                childItem.group = groupItem;
                groupItem.addChild(childItem);
            }

            mAdapter.addGroup(groupItem);
        }

        mAdapter.expand(0);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<ItemVH> {
        private ArrayList<Item> items;

        public RecyclerViewAdapter(ArrayList<Item> items) {
            this.items = items;
        }

        public void addGroup(GroupItem groupItem) {
            items.add(groupItem);
        }

        @Override
        public ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            ItemVH itemVH = null;
            switch (viewType) {
                case GROUP:
                    view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                    itemVH = new GroupVH(view);
                    break;

                case CHILD:
                    view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                    itemVH = new ChildVH(view);
                    break;
            }

            return itemVH;
        }

        @Override
        public void onBindViewHolder(ItemVH holder, final int position) {
            int viewType = getItemViewType(position);
            Item item = items.get(position);

            switch (viewType) {
                case GROUP:
                    final GroupItem groupItem = (GroupItem) item;
                    GroupVH groupVH = (GroupVH) holder;
                    groupVH.text1.setText(groupItem.title);
                    groupVH.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            action(groupItem, position);
                        }
                    });

                    break;

                case CHILD:
                    ChildItem childItem = (ChildItem) item;
                    ChildVH childVH = (ChildVH) holder;
                    childVH.text1.setText(childItem.id + "");
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).type();
        }

        public void expand(int groupOrder) {
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if (item.type() == GROUP) {
                    GroupItem groupItem = (GroupItem) item;
                    if (groupItem.id == groupOrder) {
                        items.addAll(i + 1, groupItem.children);
                        groupItem.isExpand = true;
                    }
                }
            }

            mAdapter.notifyDataSetChanged();
        }

        private void action(GroupItem groupItem, int position) {
            groupItem.isExpand = !groupItem.isExpand;

            if (groupItem.isExpand) {
                items.addAll(position + 1, groupItem.children);
            } else {
                Iterator<Item> iterator = items.iterator();
                while (iterator.hasNext()) {
                    Item it = iterator.next();
                    if (it.type() == CHILD) {
                        ChildItem cid = (ChildItem) it;
                        if (cid.group.id == groupItem.id)
                            iterator.remove();
                    }
                }
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    private abstract class Item {
        public int id;

        public abstract int type();
    }

    private abstract class ItemVH extends RecyclerView.ViewHolder {

        public ItemVH(View itemView) {
            super(itemView);
        }

        public abstract int type();
    }

    private class ChildItem extends Item {
        public GroupItem group;

        @Override
        public int type() {
            return CHILD;
        }
    }

    private class GroupItem extends Item {
        public String title;

        public boolean isExpand = false;
        public ArrayList<ChildItem> children;

        public GroupItem() {
            children = new ArrayList<>();
        }

        @Override
        public int type() {
            return GROUP;
        }

        public void addChild(ChildItem childItem) {
            children.add(childItem);
        }
    }

    private class ChildVH extends ItemVH {
        public TextView text1;

        public ChildVH(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text1.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public int type() {
            return CHILD;
        }
    }

    private class GroupVH extends ItemVH {
        private TextView text1;

        public GroupVH(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text1.setTextColor(Color.WHITE);
            itemView.setBackgroundColor(Color.RED);
        }

        @Override
        public int type() {
            return GROUP;
        }
    }
}