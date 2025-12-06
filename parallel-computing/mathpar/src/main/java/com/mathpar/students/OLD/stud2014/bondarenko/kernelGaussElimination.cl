__kernel void swap(__global float *A, int pivot_id){
    int index;
    __local float *temp;
    __local float *temp2;
    event_t evt[2];

    int pivot_row_index = pivot_id * (N + 1);
    int swap_row_index;

    int swap_id = pivot_id;
    index = swap_id * (N + 1) + pivot_id;

    while(swap_id < N && fabs(A[index]) < 0.00001){
        swap_id++;
        index += (N + 1);
    }

    swap_row_index = swap_id * (N + 1);

    if(swap_row_index != pivot_row_index && fabs(A[swap_row_index + pivot_id]) >= 0.00001){

        evt[0] = async_work_group_copy((__global float*)(&A[pivot_row_index]), temp2, N + 1, 0);
        evt[1] = async_work_group_copy((__global float*)(&A[swap_row_index]), temp, N + 1, 0);
        wait_group_events(2, evt);

        evt[0] = async_work_group_copy(temp, (__global float*)(&A[pivot_row_index]), N + 1, 0);
        evt[1] = async_work_group_copy(temp2, (__global float*)(&A[swap_row_index]), N + 1, 0);
        wait_group_events(2, evt);
    }
}

__kernel void direct(__global float *A, int pivot_id){
    __const int row_id = get_global_id(0) + pivot_id + 1;
    __const int pivot = pivot_id * (N + 1) + pivot_id;
    __const int me = row_id * (N + 1) + pivot_id;

    if(fabs(A[pivot]) < 0.00001){
        return;
    }

    __const float multiplier = A[me] / A[pivot];

    __const int start = 0;
    __const int end = N + 2;

    for(int i = start; i < end; i++){
        A[row_id*(N+1) + i] -= A[pivot_id*(N+1) + i] * multiplier;
    }
}

__kernel void reverse(__global float *A, int sltnIndex, __global float *solution){
    __local float sltn;

    float sum_prev = 0;
    __const int sltn_index = sltnIndex * (N + 1) + sltnIndex;
    __const int b_index = sltnIndex * (N + 1) + N;

    for (int col_id = (sltnIndex + 1); col_id < N; col_id++) {
        sum_prev += A[sltnIndex * (N + 1) + col_id] * solution[col_id];
    }

    solution[sltnIndex] = (A[b_index] - sum_prev) / A[sltn_index];
}